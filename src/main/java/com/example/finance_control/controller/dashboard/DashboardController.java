package com.example.finance_control.controller.dashboard;

import com.example.finance_control.domain.category.Category;
import com.example.finance_control.dto.dashboard.*;
import com.example.finance_control.service.DashboardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Endpoint principal do dashboard - retorna todos os dados necessários para a tela inicial
     *
     * @param userId ID do usuário (obrigatório)
     * @param period Período predefinido (opcional): LAST_7_DAYS, LAST_30_DAYS, CURRENT_MONTH, LAST_MONTH, CURRENT_YEAR
     * @param startDate Data inicial personalizada (opcional, usado quando period = CUSTOM)
     * @param endDate Data final personalizada (opcional, usado quando period = CUSTOM)
     * @return Dados completos do dashboard
     */
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard(
            @RequestParam @Pattern(
                    regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "ID do usuário deve ser um UUID válido"
            ) String userId,

            @RequestParam(required = false, defaultValue = "CURRENT_MONTH") String period,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) {

        log.info("Dashboard request for user: {}, period: {}", userId, period);

        DashboardFilterDTO filter = DashboardFilterDTO.builder()
                .userId(userId)
                .period(period)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        DashboardResponseDTO dashboard = dashboardService.getDashboardData(filter);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Endpoint para dados do dashboard com filtros avançados
     *
     * @param filter Filtros personalizados via POST body
     * @return Dados do dashboard filtrados
     */
    @PostMapping("/filtered")
    public ResponseEntity<DashboardResponseDTO> getDashboardFiltered(
            @RequestBody @Valid DashboardFilterDTO filter) {

        log.info("Filtered dashboard request for user: {}", filter.getUserId());

        DashboardResponseDTO dashboard = dashboardService.getDashboardData(filter);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Endpoint para resumo detalhado de uma categoria específica
     *
     * @param userId ID do usuário
     * @param category Categoria a ser analisada
     * @param period Período de análise (opcional, padrão: CURRENT_MONTH)
     * @return Resumo detalhado da categoria
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<CategorySummaryDTO> getCategorySummary(
            @RequestParam @Pattern(
                    regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "ID do usuário deve ser um UUID válido"
            ) String userId,

            @PathVariable Category category,

            @RequestParam(required = false, defaultValue = "CURRENT_MONTH") String period) {

        log.info("Category summary request - User: {}, Category: {}, Period: {}",
                userId, category, period);

        CategorySummaryDTO summary = dashboardService.getCategorySummary(userId, category, period);
        return ResponseEntity.ok(summary);
    }

    /**
     * Endpoint para obter resumo financeiro rápido (sem gráficos detalhados)
     * Útil para widgets ou notificações
     *
     * @param userId ID do usuário
     * @param period Período de análise
     * @return Apenas o resumo financeiro
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardResponseDTO.FinancialSummary> getFinancialSummary(
            @RequestParam @Pattern(
                    regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "ID do usuário deve ser um UUID válido"
            ) String userId,

            @RequestParam(required = false, defaultValue = "CURRENT_MONTH") String period) {

        log.info("Financial summary request for user: {}, period: {}", userId, period);

        DashboardFilterDTO filter = DashboardFilterDTO.builder()
                .userId(userId)
                .period(period)
                .build();

        DashboardResponseDTO dashboard = dashboardService.getDashboardData(filter);
        return ResponseEntity.ok(dashboard.getFinancialSummary());
    }

    /**
     * Endpoint para obter apenas as estatísticas rápidas
     *
     * @param userId ID do usuário
     * @param period Período de análise
     * @return Apenas as estatísticas rápidas
     */
    @GetMapping("/quick-stats")
    public ResponseEntity<DashboardResponseDTO.QuickStats> getQuickStats(
            @RequestParam @Pattern(
                    regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "ID do usuário deve ser um UUID válido"
            ) String userId,

            @RequestParam(required = false, defaultValue = "CURRENT_MONTH") String period) {

        log.info("Quick stats request for user: {}, period: {}", userId, period);

        DashboardFilterDTO filter = DashboardFilterDTO.builder()
                .userId(userId)
                .period(period)
                .build();

        DashboardResponseDTO dashboard = dashboardService.getDashboardData(filter);
        return ResponseEntity.ok(dashboard.getQuickStats());
    }

    /**
     * Endpoint para obter evolução mensal
     * Útil para gráficos específicos
     *
     * @param userId ID do usuário
     * @param months Quantidade de meses para retroceder (padrão: 6)
     * @return Lista com evolução mensal
     */
    @GetMapping("/evolution")
    public ResponseEntity<List<DashboardResponseDTO.MonthlyEvolution>> getMonthlyEvolution(
            @RequestParam @Pattern(
                    regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "ID do usuário deve ser um UUID válido"
            ) String userId,

            @RequestParam(required = false, defaultValue = "6") Integer months) {

        log.info("Monthly evolution request for user: {}, months: {}", userId, months);

        //Calcular período baseado na quantidade de meses
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months);

        DashboardFilterDTO filter = DashboardFilterDTO.builder()
                .userId(userId)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        DashboardResponseDTO dashboard = dashboardService.getDashboardData(filter);
        return ResponseEntity.ok(dashboard.getMonthlyEvolution());
    }

    /**
     * Endpoint para obter distribuição por categorias
     *
     * @param userId ID do usuário
     * @param type Tipo (EXPENSE ou REVENUE)
     * @param period Período de análise
     * @return Lista de distribuição por categorias
     */
    @GetMapping("/categories/{type}")
    public ResponseEntity<List<DashboardResponseDTO.CategoryDistribution>> getCategoryDistribution(
            @RequestParam @Pattern(
                    regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "ID do usuário deve ser um UUID válido"
            ) String userId,

            @PathVariable("type") String type,

            @RequestParam(required = false, defaultValue = "CURRENT_MONTH") String period) {

        log.info("Category distribution request - User: {}, Type: {}, Period: {}",
                userId, type, period);

        DashboardFilterDTO filter = DashboardFilterDTO.builder()
                .userId(userId)
                .period(period)
                .build();

        DashboardResponseDTO dashboard = dashboardService.getDashboardData(filter);

        if ("EXPENSE".equalsIgnoreCase(type)) {
            return ResponseEntity.ok(dashboard.getExpensesByCategory());
        } else if ("REVENUE".equalsIgnoreCase(type)) {
            return ResponseEntity.ok(dashboard.getRevenuesByCategory());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}