package com.example.finance_control.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(

        @NotNull(message = "Email cannot be null")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Password cannot be null")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password) {
}
