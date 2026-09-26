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
public class Track {
    private final UUID id;
    private final String title;
    private final String description;
    private final String targetDepartment;
    private final Integer estimatedHours;
    private final Boolean isActive;
    private final List<Module> modules;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
