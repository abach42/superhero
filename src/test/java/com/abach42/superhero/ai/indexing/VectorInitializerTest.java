package com.abach42.superhero.ai.indexing;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VectorInitializerTest {

    @Mock
    private VectorService vectorService;

    @Test
    @DisplayName("Should initialize vector store successfully on first try")
    void shouldInitializeOnFirstTry() {
        VectorInitializer subject = new VectorInitializer(vectorService);

        subject.initializeVectorStore();

        verify(vectorService, times(1)).updateSuperheroes();
    }

    @Test
    @DisplayName("Should stop retries when thread is interrupted during backoff")
    void shouldStopWhenInterrupted() {
        VectorInitializer subject = new VectorInitializer(vectorService);
        doThrow(new RuntimeException("boom")).when(vectorService).updateSuperheroes();
        Thread.currentThread().interrupt();

        subject.initializeVectorStore();

        verify(vectorService, times(1)).updateSuperheroes();
        Thread.interrupted();
    }
}
