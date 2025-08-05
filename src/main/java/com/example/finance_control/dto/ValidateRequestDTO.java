package com.example.finance_control.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ValidateRequestDTO(
        @NotBlank(message = "Token é obrigatório")
        @Pattern(
                regexp = "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$",
                message = "Token deve ter formato JWT válido"
        )
        String token
) {}