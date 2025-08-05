package com.example.finance_control.dto;

import com.example.finance_control.domain.type.Type;
import jakarta.validation.constraints.*;

import java.time.Instant;

public record ActivityRequestDTO(
        @NotNull(message = "Data é obrigatória")
        @PastOrPresent(message = "Data não pode ser no futuro")
        Instant date,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(min = 3, max = 255, message = "Descrição deve ter entre 3 e 255 caracteres")
        @Pattern(
                regexp = "^[\\p{L}\\p{N}\\p{P}\\p{Z}\\s]+$",
                message = "Descrição contém caracteres inválidos"
        )
        String description,

        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        @DecimalMax(value = "999999.99", message = "Valor muito alto (máximo R$ 999.999,99)")
        @Digits(integer = 6, fraction = 2, message = "Valor deve ter no máximo 6 dígitos e 2 casas decimais")
        Double value,

        @NotNull(message = "Tipo é obrigatório")
        Type type,

        @NotBlank(message = "ID do usuário é obrigatório")
        @Pattern(
                regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "ID do usuário deve ser um UUID válido"
        )
        String userId) {}