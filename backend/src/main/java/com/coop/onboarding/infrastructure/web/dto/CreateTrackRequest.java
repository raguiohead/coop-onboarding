package com.coop.onboarding.infrastructure.web.dto;

import com.coop.onboarding.application.track.CreateTrackCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateTrackRequest(
        @NotBlank(message = "O título é obrigatório")
        String title,

        String description,

        String targetDepartment,

        @PositiveOrZero(message = "A carga horária estimada deve ser maior ou igual a zero")
        Integer estimatedHours
) {
    public CreateTrackCommand toCommand() {
        return new CreateTrackCommand(
                title,
                description,
                targetDepartment,
                estimatedHours != null ? estimatedHours : 0
        );
    }
}
