package com.example.finance_control.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ValidateResponseDTO(
        @JsonProperty("is_valid") boolean isValid
) {}