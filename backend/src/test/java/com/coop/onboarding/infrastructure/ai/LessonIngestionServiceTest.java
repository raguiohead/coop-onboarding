package com.coop.onboarding.infrastructure.ai;

import com.coop.onboarding.application.ai.IngestLessonCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LessonIngestionServiceTest {

    @Mock
    private VectorStore vectorStore;

    private LessonIngestionService ingestionService;

    @BeforeEach
    void setUp() {
        ingestionService = new LessonIngestionService(vectorStore);
    }

    @Test
    @DisplayName("Ingere aula com metadados ricos (lessonId, moduleId, trackId, title, source) no VectorStore")
    void ingestsLessonWithRichMetadata() {
        UUID lessonId = UUID.randomUUID();
        UUID moduleId = UUID.randomUUID();
        UUID trackId = UUID.randomUUID();
        String title = "Introdução ao Cooperativismo de Crédito";
        String content = "# Introdução\n\nO cooperativismo de crédito é um modelo baseado na ajuda mútua.";

        IngestLessonCommand command = new IngestLessonCommand(lessonId, title, content, moduleId, trackId);

        ingestionService.execute(command);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).accept(captor.capture());

        List<Document> capturedDocs = captor.getValue();
        assertThat(capturedDocs).isNotEmpty();

        Document firstChunk = capturedDocs.getFirst();
        assertThat(firstChunk.getMetadata())
                .containsEntry("lessonId", lessonId.toString())
                .containsEntry("moduleId", moduleId.toString())
                .containsEntry("trackId", trackId.toString())
                .containsEntry("title", title)
                .containsEntry("source", "lesson:" + lessonId);

        assertThat(firstChunk.getContent()).contains("cooperativismo de crédito");
    }
}
