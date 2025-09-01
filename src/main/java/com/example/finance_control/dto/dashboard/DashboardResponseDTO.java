package com.example.finance_control.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DashboardResponseDTO {

    private final FinancialSummary financialSummary;

    private final List<CategoryDistribution> expensesByCategory;
    private final List<CategoryDistribution> revenuesByCategory;

    private final List<MonthlyEvolution> monthlyEvolution;

    private final QuickStats quickStats;

    @Getter
    @Builder
    public static class FinancialSummary {
        private final Double totalRevenues;
        private final Double totalExpenses;
        private final Double currentBalance;
        private final Double monthlyBalance;
        private final String balanceStatus; // "POSITIVE", "NEGATIVE", "NEUTRAL"

        @JsonFormat(pattern = "dd/MM/yyyy")
        private final LocalDate periodStart;

        @JsonFormat(pattern = "dd/MM/yyyy")
        private final LocalDate periodEnd;
    }

    @Getter
    @Builder
    public static class CategoryDistribution {
        private final String categoryCode;
        private final String categoryName;
        private final String emoji;
        private final String color;
        private final Double totalValue;
        private final Double percentage;
        private final Integer transactionCount;
    }

    @Getter
    @Builder
    public static class MonthlyEvolution {
        private final String month;
        private final Integer year;
        private final Double revenues;
        private final Double expenses;
        private final Double balance;

        @JsonFormat(pattern = "MM/yyyy")
        private final String periodLabel;
    }

    @Getter
    @Builder
    public static class QuickStats {
        private final Integer totalTransactions;
        private final Double averageExpense;
        private final Double averageRevenue;
        private final String topExpenseCategory;
        private final String topRevenueCategory;
        private final Integer daysWithTransactions;
        private final Double dailyAverageSpending;
    }
}