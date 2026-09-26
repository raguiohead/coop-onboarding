package com.coop.onboarding.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class User {
    private final UUID id;
    private final String keycloakId;
    private final String name;
    private final String email;
    private final UserRole role;
    private final String department;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
