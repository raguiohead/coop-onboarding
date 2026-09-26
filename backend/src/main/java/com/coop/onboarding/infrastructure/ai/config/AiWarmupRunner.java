package com.coop.onboarding.infrastructure.ai.config;

import com.coop.onboarding.application.ai.AskTutorUseCase;
import com.coop.onboarding.infrastructure.ai.TutorRagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AiWarmupRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AiWarmupRunner.class);
    private final AskTutorUseCase askTutorUseCase;

    @Autowired
    public AiWarmupRunner(@Autowired(required = false) AskTutorUseCase askTutorUseCase) {
        this.askTutorUseCase = askTutorUseCase;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (askTutorUseCase instanceof TutorRagService tutorRagService) {
            CompletableFuture.runAsync(() -> {
                try {
                    // Aguarda 3 segundos após o boot antes de aquecer
                    Thread.sleep(3000);
                    tutorRagService.warmup();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.warn("Warm-up assíncrono do Ollama ignorado: {}", e.getMessage());
                }
            });
        }
    }
}
