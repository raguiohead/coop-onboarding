package com.coop.onboarding.infrastructure.web.dto.ai;

import com.coop.onboarding.domain.ai.TutorAnswer;

import java.util.List;
import java.util.UUID;

public record AskTutorResponse(
        UUID lessonId,
        String answer,
        List<String> sources
) {
    public static AskTutorResponse fromDomain(TutorAnswer tutorAnswer) {
        return new AskTutorResponse(
                tutorAnswer.getLessonId(),
                tutorAnswer.getAnswer(),
                tutorAnswer.getSources()
        );
    }
}
