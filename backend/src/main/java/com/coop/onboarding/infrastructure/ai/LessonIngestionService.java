package com.coop.onboarding.infrastructure.ai;

import com.coop.onboarding.application.ai.IngestLessonCommand;
import com.coop.onboarding.application.ai.IngestLessonUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LessonIngestionService implements IngestLessonUseCase {

    private static final Logger log = LoggerFactory.getLogger(LessonIngestionService.class);

    private final VectorStore vectorStore;
    private final TokenTextSplitter tokenTextSplitter;

    public LessonIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.tokenTextSplitter = new TokenTextSplitter();
    }

    @Override
    public void execute(IngestLessonCommand command) {
        log.info("Iniciando ingestão semântica da aula id={}", command.lessonId());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("lessonId", command.lessonId().toString());
        if (command.moduleId() != null) {
            metadata.put("moduleId", command.moduleId().toString());
        }
        if (command.trackId() != null) {
            metadata.put("trackId", command.trackId().toString());
        }
        if (command.title() != null) {
            metadata.put("title", command.title());
        }
        metadata.put("source", "lesson:" + command.lessonId());

        String content = command.contentMarkdown() != null ? command.contentMarkdown() : "";
        Document rawDoc = new Document(content, metadata);

        List<Document> chunks = tokenTextSplitter.apply(List.of(rawDoc));
        log.info("Conteúdo da aula {} dividido em {} chunks para vetorização", command.lessonId(), chunks.size());

        vectorStore.accept(chunks);
        log.info("Ingestão semântica concluída com sucesso para aula id={}", command.lessonId());
    }
}
