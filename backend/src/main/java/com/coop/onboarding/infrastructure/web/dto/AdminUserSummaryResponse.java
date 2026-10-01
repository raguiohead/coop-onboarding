package com.coop.onboarding.infrastructure.web.dto;

import com.coop.onboarding.domain.model.UserRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AdminUserSummaryResponse(
        UUID id,
        String keycloakId,
        String name,
        String email,
        UserRole role,
        String department,
        String jobTitle,
        String phone,
        String bio,
        String onboardingStatus,
        int completedLessons,
        int totalLessons,
        int progressPercent,
        boolean enabled,
        OffsetDateTime createdAt
) {}
