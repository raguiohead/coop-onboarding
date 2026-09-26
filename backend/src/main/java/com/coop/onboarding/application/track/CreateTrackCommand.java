package com.coop.onboarding.application.track;

public record CreateTrackCommand(
        String title,
        String description,
        String targetDepartment,
        Integer estimatedHours
) {
}
