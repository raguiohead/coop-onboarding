package com.coop.onboarding.application.track.port;

import com.coop.onboarding.domain.model.Track;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrackOutputPort {
    Track save(Track track);
    Optional<Track> findById(UUID id);
    List<Track> findAll();
}
