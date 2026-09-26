package com.coop.onboarding.infrastructure.web.dto.ai;

import com.coop.onboarding.application.ai.IngestLessonCommand;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record IngestLessonRequest(
        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotBlank(message = "O conteúdo markdown é obrigatório")
        String contentMarkdown,

        UUID moduleId,
        UUID trackId
) {
    public IngestLessonCommand toCommand(UUID lessonId) {
        return new IngestLessonCommand(
                lessonId,
                title,
                contentMarkdown,
                moduleId,
                trackId
        );
    }
}
