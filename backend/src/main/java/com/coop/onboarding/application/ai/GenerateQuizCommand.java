package com.coop.onboarding.application.ai;

import java.util.UUID;

public record GenerateQuizCommand(
        UUID lessonId,
        String contentMarkdown,
        Integer questionCount
) {}
