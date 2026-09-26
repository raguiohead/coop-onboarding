package com.coop.onboarding.infrastructure.web.controller;

import com.coop.onboarding.application.ai.AskTutorUseCase;
import com.coop.onboarding.application.ai.GenerateQuizUseCase;
import com.coop.onboarding.application.ai.IngestLessonUseCase;
import com.coop.onboarding.domain.ai.GeneratedQuiz;
import com.coop.onboarding.domain.ai.TutorAnswer;
import com.coop.onboarding.infrastructure.web.dto.ai.AskTutorRequest;
import com.coop.onboarding.infrastructure.web.dto.ai.AskTutorResponse;
import com.coop.onboarding.infrastructure.web.dto.ai.GenerateQuizRequest;
import com.coop.onboarding.infrastructure.web.dto.ai.GenerateQuizResponse;
import com.coop.onboarding.infrastructure.web.dto.ai.IngestLessonRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

import com.coop.onboarding.application.ai.AskTutorQuery;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/ai")
public class AIController {

    private final IngestLessonUseCase ingestLessonUseCase;
    private final AskTutorUseCase askTutorUseCase;
    private final GenerateQuizUseCase generateQuizUseCase;

    public AIController(
            IngestLessonUseCase ingestLessonUseCase,
            AskTutorUseCase askTutorUseCase,
            GenerateQuizUseCase generateQuizUseCase
    ) {
        this.ingestLessonUseCase = ingestLessonUseCase;
        this.askTutorUseCase = askTutorUseCase;
        this.generateQuizUseCase = generateQuizUseCase;
    }

    @PostMapping("/lessons/{lessonId}/ingest")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    public ResponseEntity<Map<String, Object>> ingestLesson(
            @PathVariable UUID lessonId,
            @Valid @RequestBody IngestLessonRequest request
    ) {
        ingestLessonUseCase.execute(request.toCommand(lessonId));
        return ResponseEntity.ok(Map.of(
                "message", "Conteúdo da aula indexado com sucesso",
                "lessonId", lessonId
        ));
    }

    @PostMapping("/tutor/ask")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AskTutorResponse> askTutor(
            @Valid @RequestBody AskTutorRequest request
    ) {
        TutorAnswer answer = askTutorUseCase.execute(request.toQuery());
        return ResponseEntity.ok(AskTutorResponse.fromDomain(answer));
    }

    @GetMapping(value = "/tutor/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    public Flux<String> streamTutor(
            @RequestParam String lessonId,
            @RequestParam String question
    ) {
        UUID parsedLessonId = parseLessonId(lessonId);
        return askTutorUseCase.stream(new AskTutorQuery(parsedLessonId, question));
    }

    private UUID parseLessonId(String rawId) {
        if (rawId == null || rawId.isBlank()) {
            return UUID.fromString("d1a2b3c4-0001-4000-8000-000000000001");
        }
        try {
            return UUID.fromString(rawId);
        } catch (IllegalArgumentException e) {
            return UUID.nameUUIDFromBytes(rawId.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    @PostMapping("/quiz/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    public ResponseEntity<GenerateQuizResponse> generateQuiz(
            @Valid @RequestBody GenerateQuizRequest request
    ) {
        GeneratedQuiz quiz = generateQuizUseCase.execute(request.toCommand());
        return ResponseEntity.ok(GenerateQuizResponse.fromDomain(quiz));
    }
}
