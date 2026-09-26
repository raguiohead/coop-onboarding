package com.coop.onboarding.application.ai;

import java.util.UUID;

public record IngestLessonCommand(
        UUID lessonId,
        String title,
        String contentMarkdown,
        UUID moduleId,
        UUID trackId
) {}
