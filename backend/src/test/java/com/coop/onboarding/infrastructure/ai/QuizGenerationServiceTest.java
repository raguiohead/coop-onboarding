package com.coop.onboarding.infrastructure.ai;

import com.coop.onboarding.application.ai.GenerateQuizCommand;
import com.coop.onboarding.domain.ai.GeneratedQuiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizGenerationServiceTest {

    private ChatClient chatClient;
    private QuizGenerationService quizService;

    @BeforeEach
    void setUp() {
        chatClient = Mockito.mock(ChatClient.class, Mockito.RETURNS_DEEP_STUBS);
        quizService = new QuizGenerationService(chatClient);
    }

    @Test
    @DisplayName("Gera quiz estruturado com gabarito e justificativa a partir de JSON do LLM")
    void generatesStructuredQuizSuccessfully() {
        UUID lessonId = UUID.randomUUID();
        GenerateQuizCommand command = new GenerateQuizCommand(
                lessonId,
                "O princípio da intercooperação afirma que cooperativas atendem seus associados mais eficazmente trabalhando juntas.",
                1
        );

        String validJsonResponse = """
                {
                  "questions": [
                    {
                      "question": "O que preconiza o princípio da intercooperação?",
                      "options": [
                        "Trabalho conjunto entre cooperativas",
                        "Competição predatória no mercado",
                        "Centralização do capital",
                        "Isolamento regional"
                      ],
                      "correctAnswer": "Trabalho conjunto entre cooperativas",
                      "explanation": "A intercooperação fortalece o movimento através de estruturas locais, nacionais e internacionais."
                    }
                  ]
                }
                """;

        when(chatClient.prompt()
                .system(any(Consumer.class))
                .user(any(Consumer.class))
                .call()
                .content())
                .thenReturn(validJsonResponse);

        GeneratedQuiz quiz = quizService.execute(command);

        assertThat(quiz.getLessonId()).isEqualTo(lessonId);
        assertThat(quiz.getQuestions()).hasSize(1);

        var question = quiz.getQuestions().getFirst();
        assertThat(question.getQuestion()).isEqualTo("O que preconiza o princípio da intercooperação?");
        assertThat(question.getOptions()).hasSize(4);
        assertThat(question.getCorrectAnswer()).isEqualTo("Trabalho conjunto entre cooperativas");
        assertThat(question.getExplanation()).contains("intercooperação");
    }

    @Test
    @DisplayName("Gera quiz com sucesso mesmo se LLM envolver JSON em bloco markdown com crases")
    void generatesQuizWithMarkdownFencesSanitization() {
        UUID lessonId = UUID.randomUUID();
        GenerateQuizCommand command = new GenerateQuizCommand(
                lessonId,
                "Conteúdo da aula",
                1
        );

        String markdownWrappedResponse = """
                ```json
                {
                  "questions": [
                    {
                      "question": "Qual a regra de votação no cooperativismo?",
                      "options": ["1 cooperado = 1 voto", "1 quota = 1 voto", "Voto censitário", "Sem votação"],
                      "correctAnswer": "1 cooperado = 1 voto",
                      "explanation": "Princípio da gestão democrática cooperativa."
                    }
                  ]
                }
                ```
                """;

        when(chatClient.prompt()
                .system(any(Consumer.class))
                .user(any(Consumer.class))
                .call()
                .content())
                .thenReturn(markdownWrappedResponse);

        GeneratedQuiz quiz = quizService.execute(command);

        assertThat(quiz.getLessonId()).isEqualTo(lessonId);
        assertThat(quiz.getQuestions()).hasSize(1);
        assertThat(quiz.getQuestions().getFirst().getCorrectAnswer()).isEqualTo("1 cooperado = 1 voto");
    }
}
