package com.example.finance_control.dto.mapper;

import com.example.finance_control.domain.activity.Activity;
import com.example.finance_control.domain.user.User;
import com.example.finance_control.dto.ActivityRequestDTO;
import com.example.finance_control.dto.ActivityResponseDTO;

public class ActivityMapper {

    // Converte Activity para ActivityRequestDTO
    public static ActivityRequestDTO toDTO(Activity activity) {
        if (activity == null) {
            return null;
        }
        return new ActivityRequestDTO(
                activity.getDate(),
                activity.getDescription(),
                activity.getValue(),
                activity.getType(),
                activity.getCategory(),
                activity.getUser() != null ? activity.getUser().getId() : null
        );
    }

    // Converte ActivityRequestDTO para Activity
    public static Activity toEntity(ActivityRequestDTO dto, User user) {
        if (dto == null) {
            return null;
        }
        return Activity.create(
                null, // ou qualquer valor padrão ou gerado automaticamente
                dto.date(),
                dto.description(),
                dto.value(),
                dto.type(),
                dto.category(),
                user // precisa passar o objeto User
        );
    }

    // Converte Activity para ActivityResponseDTO
    public static ActivityResponseDTO toResponseDTO(Activity activity) {
        if (activity == null) {
            return null;
        }
        return new ActivityResponseDTO(
                activity.getId(),
                activity.getDate(),
                activity.getDescription(),
                activity.getValue(),
                activity.getType(),
                activity.getCategory(),
                new ActivityResponseDTO.CategoryInfo(activity.getCategory()),
                activity.getUser() != null ? activity.getUser().getId() : null
        );
    }
}
