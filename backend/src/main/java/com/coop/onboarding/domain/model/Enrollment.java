package com.coop.onboarding.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Enrollment {
    private final UUID id;
    private final UUID userId;
    private final UUID trackId;
    private final EnrollmentStatus status;
    private final OffsetDateTime startedAt;
    private final OffsetDateTime completedAt;
    private final LocalDate dueDate;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
