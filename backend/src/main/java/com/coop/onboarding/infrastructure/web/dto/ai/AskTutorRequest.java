package com.coop.onboarding.infrastructure.web.dto.ai;

import com.coop.onboarding.application.ai.AskTutorQuery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AskTutorRequest(
        @NotNull(message = "O ID da aula é obrigatório")
        UUID lessonId,

        @NotBlank(message = "A pergunta é obrigatória")
        String question
) {
    public AskTutorQuery toQuery() {
        return new AskTutorQuery(lessonId, question);
    }
}
