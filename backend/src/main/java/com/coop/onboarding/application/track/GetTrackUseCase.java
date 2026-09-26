package com.coop.onboarding.application.track;

import com.coop.onboarding.domain.model.Track;

import java.util.Optional;
import java.util.UUID;

public interface GetTrackUseCase {
    Optional<Track> execute(UUID trackId);
}
