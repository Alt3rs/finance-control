package com.example.finance_control.dto;

import com.example.finance_control.domain.category.Category;

public record CategoryResponseDTO(
        String code,
        String displayName,
        String emoji,
        String color,
        String description,
        boolean isRevenueCategory,
        boolean isExpenseCategory
) {
    public CategoryResponseDTO(Category category) {
        this(
                category.name(),
                category.getDisplayName(),
                category.getEmoji(),
                category.getColor(),
                category.getDescription(),
                category.isRevenueCategory(),
                category.isExpenseCategory()
        );
    }
}
