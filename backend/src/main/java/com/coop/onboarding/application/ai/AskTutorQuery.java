package com.coop.onboarding.application.ai;

import java.util.UUID;

public record AskTutorQuery(
        UUID lessonId,
        String question
) {}
