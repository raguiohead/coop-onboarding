package com.coop.onboarding.infrastructure.web.controller;

import com.coop.onboarding.application.track.CreateTrackUseCase;
import com.coop.onboarding.application.track.GetTrackUseCase;
import com.coop.onboarding.domain.model.Track;
import com.coop.onboarding.infrastructure.web.dto.CreateTrackRequest;
import com.coop.onboarding.infrastructure.web.dto.TrackResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tracks")
public class TrackController {

    private final CreateTrackUseCase createTrackUseCase;
    private final GetTrackUseCase getTrackUseCase;

    public TrackController(CreateTrackUseCase createTrackUseCase, GetTrackUseCase getTrackUseCase) {
        this.createTrackUseCase = createTrackUseCase;
        this.getTrackUseCase = getTrackUseCase;
    }

    @GetMapping
    public ResponseEntity<List<TrackResponse>> getAllTracks() {
        List<Track> tracks = getTrackUseCase.findAll();
        List<TrackResponse> responses = tracks.stream()
                .map(TrackResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrackResponse> createTrack(@Valid @RequestBody CreateTrackRequest request) {
        Track created = createTrackUseCase.execute(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(TrackResponse.fromDomain(created));
    }
}
