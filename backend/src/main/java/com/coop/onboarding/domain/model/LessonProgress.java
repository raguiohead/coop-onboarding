package com.coop.onboarding.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class LessonProgress {
    private final UUID id;
    private final UUID enrollmentId;
    private final UUID lessonId;
    private final Boolean completed;
    private final OffsetDateTime completedAt;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
