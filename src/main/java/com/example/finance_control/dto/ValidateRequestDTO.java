package com.example.finance_control.dto;

import jakarta.validation.constraints.NotBlank;

public record ValidateRequestDTO (
        @NotBlank(message = "Token should not be blank") String token
) {}
