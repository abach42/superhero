package com.abach42.superhero.ai;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class SuperheroEventListener {

    private final VectorService vectorService;

    public SuperheroEventListener(VectorService vectorService) {
        this.vectorService = vectorService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSuperheroUpdate(UpdateSuperheroVectorEvent event) {
        vectorService.updateEmbedding(event.superhero());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSuperheroUpdate(RemoveSuperheroVectorEvent event) {
        vectorService.removeEmbedding(event.superhero());
    }
}
