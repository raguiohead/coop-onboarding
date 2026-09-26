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

import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
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
    private final Map<String, TutorAnswer> answerCache = new ConcurrentHashMap<>();

    public TutorRagService(VectorStore vectorStore, ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    public void warmup() {
        try {
            log.info("Iniciando warm-up do modelo Ollama na memória...");
            chatClient.prompt()
                    .user("ping")
                    .call()
                    .content();
            log.info("Warm-up concluído com sucesso. Modelo mantido em memória (keep_alive: 24h).");
        } catch (Exception e) {
            log.warn("Warm-up não pôde ser concluído imediatamente: {}", e.getMessage());
        }
    }

    public Flux<String> stream(AskTutorQuery query) {
        log.info("Streaming de resposta do Tutor Virtual na aula id={}", query.lessonId());

        String filterExpression = "lessonId == '" + query.lessonId() + "'";
        SearchRequest searchRequest = SearchRequest.query(query.question())
                .withTopK(4)
                .withFilterExpression(filterExpression);

        List<Document> similarDocuments;
        try {
            similarDocuments = vectorStore.similaritySearch(searchRequest);
        } catch (Exception e) {
            log.error("Erro na busca por similaridade vetorial para streaming: {}", e.getMessage());
            similarDocuments = Collections.emptyList();
        }

        if (similarDocuments.isEmpty()) {
            return Flux.just("Olá! Seja muito bem-vindo ao nosso programa de formação cooperativa. Não encontrei informações específicas sobre este tópico no conteúdo cadastrado para esta aula. Por favor, consulte seu gestor ou o instrutor responsável pela trilha para obter mais detalhes!");
        }

        String context = similarDocuments.stream()
                .map(Document::getContent)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n\n---\n\n"));

        String userPrompt = String.format("""
                Conteúdo de apoio da Aula:
                %s

                Pergunta do Colaborador:
                %s
                """, context, query.question());

        try {
            return chatClient.prompt()
                    .system(TUTOR_SYSTEM_PROMPT)
                    .user(userPrompt)
                    .stream()
                    .content();
        } catch (Exception e) {
            log.error("Erro ao iniciar streaming com LLM: {}", e.getMessage(), e);
            return Flux.just("Olá! Ocorreu uma instabilidade temporária ao consultar o Tutor Virtual. Por favor, tente novamente em instantes.");
        }
    }

    @Override
    public TutorAnswer execute(AskTutorQuery query) {
        String cacheKey = query.lessonId() + ":" + query.question().trim().toLowerCase();
        TutorAnswer cached = answerCache.get(cacheKey);
        if (cached != null) {
            log.info("Resposta do Tutor recuperada do cache para aula id={}", query.lessonId());
            return cached;
        }
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

        TutorAnswer answer = TutorAnswer.builder()
                .lessonId(query.lessonId())
                .answer(generatedAnswer)
                .sources(sources)
                .build();

        answerCache.put(cacheKey, answer);
        return answer;
    }
}
