package com.coop.onboarding.infrastructure.persistence.adapter;

import com.coop.onboarding.application.track.port.TrackOutputPort;
import com.coop.onboarding.domain.model.Track;
import com.coop.onboarding.infrastructure.persistence.entity.TrackEntity;
import com.coop.onboarding.infrastructure.persistence.repository.TrackRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TrackPersistenceAdapter implements TrackOutputPort {

    private final TrackRepository trackRepository;

    public TrackPersistenceAdapter(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    @Override
    public Track save(Track track) {
        TrackEntity entity = TrackEntity.builder()
                .id(track.getId())
                .title(track.getTitle())
                .description(track.getDescription())
                .targetDepartment(track.getTargetDepartment())
                .estimatedHours(track.getEstimatedHours() != null ? track.getEstimatedHours() : 0)
                .isActive(track.getIsActive() != null ? track.getIsActive() : true)
                .build();

        TrackEntity saved = trackRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Track> findById(UUID id) {
        return trackRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Track> findAll() {
        return trackRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private Track toDomain(TrackEntity entity) {
        return Track.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .targetDepartment(entity.getTargetDepartment())
                .estimatedHours(entity.getEstimatedHours())
                .isActive(entity.getIsActive())
                .modules(Collections.emptyList())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
