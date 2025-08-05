package com.example.finance_control.dto;

import jakarta.validation.constraints.*;

public record LoginRequestDTO(

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ter formato válido (exemplo@dominio.com)")
        @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 50, message = "Senha deve ter entre 6 e 50 caracteres")
        String password) {}
