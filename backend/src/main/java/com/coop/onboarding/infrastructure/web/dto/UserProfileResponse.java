package com.coop.onboarding.infrastructure.web.dto;

import com.coop.onboarding.domain.model.User;
import com.coop.onboarding.domain.model.UserRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String keycloakId,
        String name,
        String email,
        UserRole role,
        String department,
        OffsetDateTime createdAt
) {
    public static UserProfileResponse fromDomain(User user) {
        if (user == null) {
            return null;
        }
        return new UserProfileResponse(
                user.getId(),
                user.getKeycloakId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getDepartment(),
                user.getCreatedAt()
        );
    }
}
