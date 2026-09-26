package com.coop.onboarding.infrastructure.ai;

import com.coop.onboarding.application.ai.AskTutorQuery;
import com.coop.onboarding.application.ai.AskTutorUseCase;
import com.coop.onboarding.domain.ai.TutorAnswer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TutorRagService implements AskTutorUseCase {

    private static final Logger log = LoggerFactory.getLogger(TutorRagService.class);

    private static final String TUTOR_SYSTEM_PROMPT = """
            Você é o Tutor Virtual do programa de Onboarding Cooperativo. Seu papel é acolher, encorajar e guiar os novos colaboradores com empatia, tom caloroso, clareza e precisão pedagógica.

            Diretrizes fundamentais:
            1. Responda à dúvida do colaborador baseando-se estritamente no conteúdo fornecido abaixo da aula atual.
            2. Jamais invente informações ou use conhecimentos externos que extrapolem o conteúdo da aula.
            3. Cite as seções, tópicos ou títulos das fontes fornecidas para embasar a resposta.
            4. Se a dúvida não puder ser respondida com o material desta aula, seja gentil, acolhedor e oriente o colaborador a consultar seu gestor ou instrutor da trilha cooperativa.
            """;

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public TutorRagService(VectorStore vectorStore, ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    @Override
    public TutorAnswer execute(AskTutorQuery query) {
        log.info("Processando pergunta para o Tutor Virtual na aula id={}", query.lessonId());

        String filterExpression = "lessonId == '" + query.lessonId() + "'";
        SearchRequest searchRequest = SearchRequest.query(query.question())
                .withTopK(4)
                .withFilterExpression(filterExpression);

        List<Document> similarDocuments;
        try {
            similarDocuments = vectorStore.similaritySearch(searchRequest);
        } catch (Exception e) {
            log.error("Erro na busca por similaridade vetorial para aula {}: {}", query.lessonId(), e.getMessage(), e);
            similarDocuments = Collections.emptyList();
        }

        if (similarDocuments.isEmpty()) {
            log.warn("Nenhum documento encontrado na busca vetorial para a aula {}", query.lessonId());
            return TutorAnswer.builder()
                    .lessonId(query.lessonId())
                    .answer("Olá! Seja muito bem-vindo ao nosso programa de formação cooperativa. Não encontrei informações específicas sobre este tópico no conteúdo cadastrado para esta aula. Por favor, consulte seu gestor ou o instrutor responsável pela trilha para obter mais detalhes!")
                    .sources(Collections.emptyList())
                    .build();
        }

        String context = similarDocuments.stream()
                .map(Document::getContent)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n\n---\n\n"));

        List<String> sources = similarDocuments.stream()
                .map(doc -> {
                    Object title = doc.getMetadata().get("title");
                    Object source = doc.getMetadata().get("source");
                    if (title != null && source != null) {
                        return title + " (" + source + ")";
                    } else if (title != null) {
                        return title.toString();
                    } else if (source != null) {
                        return source.toString();
                    }
                    return "Aula " + query.lessonId();
                })
                .distinct()
                .toList();

        String userPrompt = String.format("""
                Conteúdo de apoio da Aula:
                %s

                Pergunta do Colaborador:
                %s
                """, context, query.question());

        String generatedAnswer;
        try {
            generatedAnswer = chatClient.prompt()
                    .system(TUTOR_SYSTEM_PROMPT)
                    .user(userPrompt)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("Falha ao comunicar com LLM para o Tutor Virtual: {}", e.getMessage(), e);
            generatedAnswer = "Olá! Ocorreu uma instabilidade temporária ao consultar o Tutor Virtual. Por favor, tente novamente em instantes ou consulte diretamente seu gestor ou instrutor de onboarding.";
        }

        return TutorAnswer.builder()
                .lessonId(query.lessonId())
                .answer(generatedAnswer)
                .sources(sources)
                .build();
    }
}
