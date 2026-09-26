package com.coop.onboarding.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Lesson {
    private final UUID id;
    private final UUID moduleId;
    private final String title;
    private final String contentMarkdown;
    private final String videoUrl;
    private final Integer estimatedMinutes;
    private final Integer orderIndex;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
