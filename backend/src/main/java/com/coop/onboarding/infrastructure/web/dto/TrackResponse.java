package com.coop.onboarding.infrastructure.web.dto;

import com.coop.onboarding.domain.model.Track;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TrackResponse(
        UUID id,
        String title,
        String description,
        String targetDepartment,
        Integer estimatedHours,
        Boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static TrackResponse fromDomain(Track track) {
        if (track == null) {
            return null;
        }
        return new TrackResponse(
                track.getId(),
                track.getTitle(),
                track.getDescription(),
                track.getTargetDepartment(),
                track.getEstimatedHours(),
                track.getIsActive(),
                track.getCreatedAt(),
                track.getUpdatedAt()
        );
    }
}
