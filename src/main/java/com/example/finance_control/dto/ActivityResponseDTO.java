package com.example.finance_control.dto;


import com.example.finance_control.domain.type.Type;
import com.example.finance_control.domain.activity.Activity;

import java.time.Instant;

public record ActivityResponseDTO(String id, Instant date, String description, Double value, Type type, String userId) {

    public ActivityResponseDTO(Activity activity) {
        this(
                activity.getId(),
                activity.getDate(),
                activity.getDescription(),
                activity.getValue(),
                activity.getType(),
                activity.getUser() != null ? activity.getUser().getId() : null
        );
    }
}