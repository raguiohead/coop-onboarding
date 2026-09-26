package com.coop.onboarding.infrastructure.ai;

import com.coop.onboarding.application.ai.GenerateQuizCommand;
import com.coop.onboarding.application.ai.GenerateQuizUseCase;
import com.coop.onboarding.domain.ai.GeneratedQuiz;
import com.coop.onboarding.domain.ai.QuizQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizGenerationService implements GenerateQuizUseCase {

    private static final Logger log = LoggerFactory.getLogger(QuizGenerationService.class);

    public record QuizOutput(List<QuizQuestion> questions) {}

    private static final String QUIZ_SYSTEM_PROMPT = """
            Você é um especialista em design instrucional e avaliação pedagógica em programas corporativos de cooperativismo.
            Sua missão é gerar um quiz de fixação de múltipla escolha com alta precisão técnica e pedagógica, baseado estritamente no conteúdo fornecido da aula.

            Regras estritas:
            1. Gere exatamente {questionCount} questões de múltipla escolha.
            2. Cada questão deve ter exatamente 4 opções plausíveis no campo 'options'.
            3. 'correctAnswer' deve ser a resposta correta exata correspondente a uma das opções.
            4. 'explanation' deve conter uma justificativa clara, didática e pedagógica explicando o porquê da resposta correta.
            5. Siga rigorosamente o esquema JSON fornecido. Não inclua texto introdutório, nem comentários fora do JSON.

            {format}
            """;

    private final ChatClient chatClient;
    private final BeanOutputConverter<QuizOutput> outputConverter;

    public QuizGenerationService(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.outputConverter = new BeanOutputConverter<>(QuizOutput.class);
    }

    @Override
    public GeneratedQuiz execute(GenerateQuizCommand command) {
        int count = (command.questionCount() != null && command.questionCount() > 0)
                ? command.questionCount()
                : 3;
        String content = command.contentMarkdown() != null ? command.contentMarkdown() : "";

        log.info("Gerando {} questões de quiz para aula id={}", count, command.lessonId());

        String formatInstructions = outputConverter.getFormat();

        String rawResponse = chatClient.prompt()
                .system(s -> s.text(QUIZ_SYSTEM_PROMPT)
                        .param("questionCount", count)
                        .param("format", formatInstructions))
                .user(u -> u.text("Conteúdo da aula:\n{content}").param("content", content))
                .call()
                .content();

        QuizOutput quizOutput;
        try {
            quizOutput = outputConverter.convert(rawResponse);
        } catch (Exception ex) {
            log.warn("Erro ao converter resposta direta do BeanOutputConverter, tentando sanitizar markdown: {}", ex.getMessage());
            String sanitized = sanitizeJson(rawResponse);
            quizOutput = outputConverter.convert(sanitized);
        }

        List<QuizQuestion> questions = (quizOutput != null && quizOutput.questions() != null)
                ? quizOutput.questions()
                : List.of();

        return GeneratedQuiz.builder()
                .lessonId(command.lessonId())
                .questions(questions)
                .build();
    }

    private String sanitizeJson(String raw) {
        if (raw == null) {
            return "{}";
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }
}
