package com.coop.onboarding.infrastructure.ai;

import com.coop.onboarding.application.ai.AskTutorQuery;
import com.coop.onboarding.domain.ai.TutorAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TutorRagServiceTest {

    @Mock
    private VectorStore vectorStore;

    private ChatClient chatClient;
    private TutorRagService tutorRagService;

    @BeforeEach
    void setUp() {
        chatClient = Mockito.mock(ChatClient.class, Mockito.RETURNS_DEEP_STUBS);
        tutorRagService = new TutorRagService(vectorStore, chatClient);
    }

    @Test
    @DisplayName("Executa busca vetorial com filtro estrito de lessonId e sintetiza resposta com fontes")
    void executesRAGWithStrictLessonFilterAndCitesSources() {
        UUID lessonId = UUID.randomUUID();
        AskTutorQuery query = new AskTutorQuery(lessonId, "Quais são os 7 princípios do cooperativismo?");

        Document doc = new Document(
                "1. Adesão livre; 2. Gestão democrática...",
                Map.of(
                        "lessonId", lessonId.toString(),
                        "title", "Princípios do Cooperativismo",
                        "source", "lesson:" + lessonId
                )
        );

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc));
        when(chatClient.prompt().system(anyString()).user(anyString()).call().content())
                .thenReturn("Os princípios do cooperativismo incluem a adesão voluntária e livre e a gestão democrática.");

        TutorAnswer answer = tutorRagService.execute(query);

        assertThat(answer.getLessonId()).isEqualTo(lessonId);
        assertThat(answer.getAnswer()).contains("adesão voluntária e livre");
        assertThat(answer.getSources()).contains("Princípios do Cooperativismo (lesson:" + lessonId + ")");

        ArgumentCaptor<SearchRequest> requestCaptor = ArgumentCaptor.forClass(SearchRequest.class);
        verify(vectorStore).similaritySearch(requestCaptor.capture());

        SearchRequest captured = requestCaptor.getValue();
        assertThat(captured.getQuery()).isEqualTo(query.question());
        assertThat(captured.getTopK()).isEqualTo(4);
        assertThat(captured.getFilterExpression().toString()).contains(lessonId.toString());
    }

    @Test
    @DisplayName("Retorna resposta acolhedora de fallback quando não há documentos indexados")
    void returnsWelcomingFallbackWhenNoDocumentsFound() {
        UUID lessonId = UUID.randomUUID();
        AskTutorQuery query = new AskTutorQuery(lessonId, "Qual a taxa de juros?");

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());

        TutorAnswer answer = tutorRagService.execute(query);

        assertThat(answer.getLessonId()).isEqualTo(lessonId);
        assertThat(answer.getAnswer()).contains("Não encontrei informações específicas sobre este tópico");
        assertThat(answer.getSources()).isEmpty();
    }
}
