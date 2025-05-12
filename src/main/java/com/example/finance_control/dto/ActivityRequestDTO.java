package com.example.finance_control.dto;

import com.example.finance_control.domain.type.Type;

import java.time.Instant;

public record ActivityRequestDTO(Instant date, String description, Double value, Type type, String userId) {
}