package com.coop.onboarding.application.track.service;

import com.coop.onboarding.application.track.CreateTrackCommand;
import com.coop.onboarding.application.track.CreateTrackUseCase;
import com.coop.onboarding.application.track.GetTrackUseCase;
import com.coop.onboarding.application.track.port.TrackOutputPort;
import com.coop.onboarding.domain.model.Track;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TrackService implements CreateTrackUseCase, GetTrackUseCase {

    private final TrackOutputPort trackOutputPort;

    public TrackService(TrackOutputPort trackOutputPort) {
        this.trackOutputPort = trackOutputPort;
    }

    @Override
    public Track execute(CreateTrackCommand command) {
        Track track = Track.builder()
                .title(command.title())
                .description(command.description())
                .targetDepartment(command.targetDepartment())
                .estimatedHours(command.estimatedHours() != null ? command.estimatedHours() : 0)
                .isActive(true)
                .modules(new ArrayList<>())
                .build();
        return trackOutputPort.save(track);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Track> execute(UUID trackId) {
        return trackOutputPort.findById(trackId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Track> findAll() {
        return trackOutputPort.findAll();
    }
}
