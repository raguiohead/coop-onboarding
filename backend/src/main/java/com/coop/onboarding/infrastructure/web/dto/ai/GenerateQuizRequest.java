package com.coop.onboarding.infrastructure.web.dto.ai;

import com.coop.onboarding.application.ai.GenerateQuizCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record GenerateQuizRequest(
        @NotNull(message = "O ID da aula é obrigatório")
        UUID lessonId,

        String contentMarkdown,

        @Positive(message = "A quantidade de questões deve ser maior que zero")
        Integer questionCount
) {
    public GenerateQuizCommand toCommand() {
        return new GenerateQuizCommand(
                lessonId,
                contentMarkdown,
                questionCount != null ? questionCount : 3
        );
    }
}
