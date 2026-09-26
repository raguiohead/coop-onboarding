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

    @PostMapping("/quiz/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    public ResponseEntity<GenerateQuizResponse> generateQuiz(
            @Valid @RequestBody GenerateQuizRequest request
    ) {
        GeneratedQuiz quiz = generateQuizUseCase.execute(request.toCommand());
        return ResponseEntity.ok(GenerateQuizResponse.fromDomain(quiz));
    }
}
