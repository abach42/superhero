package com.abach42.superhero.ai.indexing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@EnableAsync
public class VectorInitializer {

    private static final Logger logger = LoggerFactory.getLogger(VectorInitializer.class);
    private final VectorService vectorService;

    public VectorInitializer(VectorService vectorService) {
        this.vectorService = vectorService;
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void initializeVectorStore() {
        int maxRetries = 10;
        int delay = 5000;

        for (int i = 0; i < maxRetries; i++) {
            try {
                logger.info("Starting semantic indexing (attempt {}/{})...", i + 1, maxRetries);
                vectorService.updateSuperheroes();
                logger.info("Semantic indexing completed successfully.");
                return;
            } catch (Exception e) {
                logger.warn("Semantic indexing failed: {}. Retrying in {}ms...", e.getMessage(),
                        delay);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        logger.error("Semantic indexing failed after {} attempts.", maxRetries);
    }
}
