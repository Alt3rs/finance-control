package com.example.finance_control.dto.dashboard;

import com.example.finance_control.domain.category.Category;
import com.example.finance_control.domain.type.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DashboardFilterDTO {

    @NotBlank(message = "ID do usuário é obrigatório")
    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "ID do usuário deve ser um UUID válido"
    )
    private final String userId;

    private final LocalDate startDate;

    private final LocalDate endDate;

    private final List<Category> categories;
    private final Type type; // null = ambos, REVENUE ou EXPENSE
    private final String period; // "LAST_7_DAYS", "LAST_30_DAYS", "CURRENT_MONTH", "LAST_MONTH", "CURRENT_YEAR", "CUSTOM"
}
