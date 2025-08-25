package com.example.finance_control.dto;

import com.example.finance_control.domain.category.Category;

public record CategoryReportDTO(
        String categoryCode,
        String categoryName,
        String emoji,
        String color,
        Double totalValue,
        Double percentage
) {
    public CategoryReportDTO(Category category, Double totalValue) {
        this(
                category.name(),
                category.getDisplayName(),
                category.getEmoji(),
                category.getColor(),
                totalValue,
                null // percentage será calculada no service se necessário
        );
    }

    // Construtor com porcentagem
    public CategoryReportDTO(Category category, Double totalValue, Double percentage) {
        this(
                category.name(),
                category.getDisplayName(),
                category.getEmoji(),
                category.getColor(),
                totalValue,
                percentage
        );
    }
}