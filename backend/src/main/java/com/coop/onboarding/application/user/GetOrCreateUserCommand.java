package com.coop.onboarding.application.user;

import com.coop.onboarding.domain.model.UserRole;

public record GetOrCreateUserCommand(
        String keycloakId,
        String name,
        String email,
        UserRole role,
        String department
) {
}
