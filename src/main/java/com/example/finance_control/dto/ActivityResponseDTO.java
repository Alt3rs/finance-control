package com.example.finance_control.dto;


import com.example.finance_control.domain.category.Category;
import com.example.finance_control.domain.type.Type;
import com.example.finance_control.domain.activity.Activity;

import java.time.Instant;

public record ActivityResponseDTO(
        String id,
        Instant date,
        String description,
        Double value,
        Type type,
        Category category,
        CategoryInfo categoryInfo,
        String userId) {

    public ActivityResponseDTO(Activity activity) {
        this(
                activity.getId(),
                activity.getDate(),
                activity.getDescription(),
                activity.getValue(),
                activity.getType(),
                activity.getCategory(),
                new CategoryInfo(activity.getCategory()),
                activity.getUser() != null ? activity.getUser().getId() : null
        );
    }

    // DTO interno para informações da categoria
    public record CategoryInfo(
            String displayName,
            String emoji,
            String color,
            String description
    ) {
        public CategoryInfo(Category category) {
            this(
                    category.getDisplayName(),
                    category.getEmoji(),
                    category.getColor(),
                    category.getDescription()
            );
        }
    }
}