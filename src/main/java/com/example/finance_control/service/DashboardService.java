package com.example.finance_control.service;

import com.example.finance_control.domain.activity.Activity;
import com.example.finance_control.domain.category.Category;
import com.example.finance_control.domain.type.Type;
import com.example.finance_control.domain.user.User;
import com.example.finance_control.dto.dashboard.*;
import com.example.finance_control.exceptions.ResourceNotFoundException;
import com.example.finance_control.repository.activity.ActivityRepository;
import com.example.finance_control.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DashboardService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    public DashboardResponseDTO getDashboardData(DashboardFilterDTO filter) {
        log.info("Generating dashboard data for user: {}", filter.getUserId());

        //Validar se o usuário existe
        User user = userRepository.findById(filter.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        //Determinar período de análise
        DateRange dateRange = calculateDateRange(filter);

        //Buscar atividades do período
        List<Activity> activities = getActivitiesInPeriod(filter.getUserId(), dateRange);

        //Filtrar por categorias se especificado
        if (filter.getCategories() != null && !filter.getCategories().isEmpty()) {
            activities = activities.stream()
                    .filter(activity -> filter.getCategories().contains(activity.getCategory()))
                    .collect(Collectors.toList());
        }

        //Filtrar por tipo se especificado
        if (filter.getType() != null) {
            activities = activities.stream()
                    .filter(activity -> activity.getType() == filter.getType())
                    .collect(Collectors.toList());
        }

        return DashboardResponseDTO.builder()
                .financialSummary(buildFinancialSummary(activities, dateRange))
                .expensesByCategory(buildCategoryDistribution(activities, Type.EXPENSE))
                .revenuesByCategory(buildCategoryDistribution(activities, Type.REVENUE))
                .monthlyEvolution(buildMonthlyEvolution(activities))
                .quickStats(buildQuickStats(activities, dateRange))
                .build();
    }

    public CategorySummaryDTO getCategorySummary(String userId, Category category, String period) {
        log.info("Generating category summary for user: {}, category: {}", userId, category);

        //Validar usuário
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        DateRange dateRange = calculateDateRange(period);
        List<Activity> activities = getActivitiesInPeriod(userId, dateRange)
                .stream()
                .filter(activity -> activity.getCategory() == category)
                .collect(Collectors.toList());

        if (activities.isEmpty()) {
            return CategorySummaryDTO.builder()
                    .categoryCode(category.name())
                    .categoryName(category.getDisplayName())
                    .emoji(category.getEmoji())
                    .color(category.getColor())
                    .totalValue(0.0)
                    .percentage(0.0)
                    .transactionCount(0)
                    .averageTransactionValue(0.0)
                    .recentTransactions(Collections.emptyList())
                    .build();
        }

        Double totalValue = activities.stream()
                .mapToDouble(Activity::getValue)
                .sum();

        List<CategorySummaryDTO.RecentTransaction> recentTransactions = activities.stream()
                .sorted((a1, a2) -> a2.getDate().compareTo(a1.getDate()))
                .limit(5)
                .map(activity -> CategorySummaryDTO.RecentTransaction.builder()
                        .id(activity.getId())
                        .description(activity.getDescription())
                        .value(activity.getValue())
                        .date(formatDate(activity.getDate()))
                        .build())
                .collect(Collectors.toList());

        return CategorySummaryDTO.builder()
                .categoryCode(category.name())
                .categoryName(category.getDisplayName())
                .emoji(category.getEmoji())
                .color(category.getColor())
                .totalValue(totalValue)
                .percentage(100.0) //Para uma categoria específica, é sempre 100%
                .transactionCount(activities.size())
                .averageTransactionValue(totalValue / activities.size())
                .recentTransactions(recentTransactions)
                .build();
    }

    private DashboardResponseDTO.FinancialSummary buildFinancialSummary(
            List<Activity> activities, DateRange dateRange) {

        Double totalRevenues = activities.stream()
                .filter(activity -> activity.getType() == Type.REVENUE)
                .mapToDouble(Activity::getValue)
                .sum();

        Double totalExpenses = activities.stream()
                .filter(activity -> activity.getType() == Type.EXPENSE)
                .mapToDouble(Activity::getValue)
                .sum();

        Double currentBalance = totalRevenues - totalExpenses;

        String balanceStatus;
        if (currentBalance > 0) {
            balanceStatus = "POSITIVE";
        } else if (currentBalance < 0) {
            balanceStatus = "NEGATIVE";
        } else {
            balanceStatus = "NEUTRAL";
        }

        return DashboardResponseDTO.FinancialSummary.builder()
                .totalRevenues(totalRevenues)
                .totalExpenses(totalExpenses)
                .currentBalance(currentBalance)
                .monthlyBalance(currentBalance) //Por simplicidade, considerando o mesmo valor
                .balanceStatus(balanceStatus)
                .periodStart(dateRange.start)
                .periodEnd(dateRange.end)
                .build();
    }

    private List<DashboardResponseDTO.CategoryDistribution> buildCategoryDistribution(
            List<Activity> activities, Type type) {

        List<Activity> filteredActivities = activities.stream()
                .filter(activity -> activity.getType() == type)
                .collect(Collectors.toList());

        if (filteredActivities.isEmpty()) {
            return Collections.emptyList();
        }

        Double totalValue = filteredActivities.stream()
                .mapToDouble(Activity::getValue)
                .sum();

        Map<Category, List<Activity>> groupedByCategory = filteredActivities.stream()
                .collect(Collectors.groupingBy(Activity::getCategory));

        return groupedByCategory.entrySet().stream()
                .map(entry -> {
                    Category category = entry.getKey();
                    List<Activity> categoryActivities = entry.getValue();

                    Double categoryTotal = categoryActivities.stream()
                            .mapToDouble(Activity::getValue)
                            .sum();

                    Double percentage = (categoryTotal / totalValue) * 100;

                    return DashboardResponseDTO.CategoryDistribution.builder()
                            .categoryCode(category.name())
                            .categoryName(category.getDisplayName())
                            .emoji(category.getEmoji())
                            .color(category.getColor())
                            .totalValue(categoryTotal)
                            .percentage(percentage)
                            .transactionCount(categoryActivities.size())
                            .build();
                })
                .sorted((c1, c2) -> Double.compare(c2.getTotalValue(), c1.getTotalValue()))
                .collect(Collectors.toList());
    }

    private List<DashboardResponseDTO.MonthlyEvolution> buildMonthlyEvolution(List<Activity> activities) {
        Map<String, List<Activity>> groupedByMonth = activities.stream()
                .collect(Collectors.groupingBy(activity -> {
                    LocalDate date = activity.getDate().atZone(ZoneId.systemDefault()).toLocalDate();
                    return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                }));

        return groupedByMonth.entrySet().stream()
                .map(entry -> {
                    String[] yearMonth = entry.getKey().split("-");
                    Integer year = Integer.parseInt(yearMonth[0]);
                    Integer month = Integer.parseInt(yearMonth[1]);

                    List<Activity> monthActivities = entry.getValue();

                    Double revenues = monthActivities.stream()
                            .filter(activity -> activity.getType() == Type.REVENUE)
                            .mapToDouble(Activity::getValue)
                            .sum();

                    Double expenses = monthActivities.stream()
                            .filter(activity -> activity.getType() == Type.EXPENSE)
                            .mapToDouble(Activity::getValue)
                            .sum();

                    return DashboardResponseDTO.MonthlyEvolution.builder()
                            .month(String.format("%02d", month))
                            .year(year)
                            .revenues(revenues)
                            .expenses(expenses)
                            .balance(revenues - expenses)
                            .periodLabel(String.format("%02d/%d", month, year))
                            .build();
                })
                .sorted((m1, m2) -> {
                    int yearCompare = m1.getYear().compareTo(m2.getYear());
                    if (yearCompare != 0) return yearCompare;
                    return m1.getMonth().compareTo(m2.getMonth());
                })
                .collect(Collectors.toList());
    }

    private DashboardResponseDTO.QuickStats buildQuickStats(List<Activity> activities, DateRange dateRange) {
        if (activities.isEmpty()) {
            return DashboardResponseDTO.QuickStats.builder()
                    .totalTransactions(0)
                    .averageExpense(0.0)
                    .averageRevenue(0.0)
                    .topExpenseCategory("")
                    .topRevenueCategory("")
                    .daysWithTransactions(0)
                    .dailyAverageSpending(0.0)
                    .build();
        }

        List<Activity> expenses = activities.stream()
                .filter(activity -> activity.getType() == Type.EXPENSE)
                .collect(Collectors.toList());

        List<Activity> revenues = activities.stream()
                .filter(activity -> activity.getType() == Type.REVENUE)
                .collect(Collectors.toList());

        Double averageExpense = expenses.isEmpty() ? 0.0 :
                expenses.stream().mapToDouble(Activity::getValue).average().orElse(0.0);

        Double averageRevenue = revenues.isEmpty() ? 0.0 :
                revenues.stream().mapToDouble(Activity::getValue).average().orElse(0.0);

        String topExpenseCategory = getTopCategory(expenses);
        String topRevenueCategory = getTopCategory(revenues);

        Set<LocalDate> uniqueDates = activities.stream()
                .map(activity -> activity.getDate().atZone(ZoneId.systemDefault()).toLocalDate())
                .collect(Collectors.toSet());

        long daysBetween = ChronoUnit.DAYS.between(dateRange.start, dateRange.end) + 1;
        Double totalExpenseValue = expenses.stream().mapToDouble(Activity::getValue).sum();
        Double dailyAverageSpending = daysBetween > 0 ? totalExpenseValue / daysBetween : 0.0;

        return DashboardResponseDTO.QuickStats.builder()
                .totalTransactions(activities.size())
                .averageExpense(averageExpense)
                .averageRevenue(averageRevenue)
                .topExpenseCategory(topExpenseCategory)
                .topRevenueCategory(topRevenueCategory)
                .daysWithTransactions(uniqueDates.size())
                .dailyAverageSpending(dailyAverageSpending)
                .build();
    }

    private String getTopCategory(List<Activity> activities) {
        if (activities.isEmpty()) return "";

        return activities.stream()
                .collect(Collectors.groupingBy(
                        Activity::getCategory,
                        Collectors.summingDouble(Activity::getValue)))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey().getDisplayName())
                .orElse("");
    }

    private DateRange calculateDateRange(DashboardFilterDTO filter) {
        //Priorizar datas explícitas se fornecidas
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            return new DateRange(filter.getStartDate(), filter.getEndDate());
        } else if (filter.getPeriod() != null) {
            return calculateDateRange(filter.getPeriod());
        } else {
            return calculateDateRange("CURRENT_MONTH");
        }
    }

    private DateRange calculateDateRange(String period) {
        LocalDate now = LocalDate.now();

        return switch (period) {
            case "LAST_7_DAYS" -> new DateRange(now.minusDays(7), now);
            case "LAST_30_DAYS" -> new DateRange(now.minusDays(30), now);
            case "CURRENT_MONTH" -> new DateRange(now.withDayOfMonth(1), now);
            case "LAST_MONTH" -> {
                LocalDate firstDayLastMonth = now.minusMonths(1).withDayOfMonth(1);
                LocalDate lastDayLastMonth = firstDayLastMonth.withDayOfMonth(
                        firstDayLastMonth.lengthOfMonth());
                yield new DateRange(firstDayLastMonth, lastDayLastMonth);
            }
            case "CURRENT_YEAR" -> new DateRange(now.withDayOfYear(1), now);
            default -> new DateRange(now.minusDays(30), now);
        };
    }

    private List<Activity> getActivitiesInPeriod(String userId, DateRange dateRange) {
        Instant startInstant = dateRange.start.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = dateRange.end.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

        return activityRepository.findByUserIdAndDateBetween(userId, startInstant, endInstant);
    }

    private String formatDate(Instant instant) {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy")
                .format(instant.atZone(ZoneId.systemDefault()).toLocalDate());
    }

    private static class DateRange {
        final LocalDate start;
        final LocalDate end;

        DateRange(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }
    }
}