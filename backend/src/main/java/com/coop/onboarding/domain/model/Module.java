package com.coop.onboarding.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Module {
    private final UUID id;
    private final UUID trackId;
    private final String title;
    private final String description;
    private final Integer orderIndex;
    private final List<Lesson> lessons;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
