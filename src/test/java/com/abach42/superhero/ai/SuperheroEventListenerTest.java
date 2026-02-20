package com.abach42.superhero.ai;

import static org.mockito.Mockito.verify;

import com.abach42.superhero.ai.indexing.UpdateSuperheroVectorEvent;
import com.abach42.superhero.ai.indexing.VectorService;
import com.abach42.superhero.superhero.Superhero;
import com.abach42.superhero.testconfiguration.TestStubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SuperheroEventListenerTest {

    @Mock
    private VectorService vectorService;

    private SuperheroEventListener subject;

    @BeforeEach
    void setUp() {
        subject = new SuperheroEventListener(vectorService);
    }

    @Test
    @DisplayName("Should handle UpdateSuperheroVectorEvent")
    void shouldHandleUpdateSuperheroVectorEvent() {
        Superhero hero = TestStubs.getSuperheroStub();
        UpdateSuperheroVectorEvent event = new UpdateSuperheroVectorEvent(hero);

        subject.handleSuperheroUpdate(event);

        verify(vectorService).updateEmbedding(hero);
    }

    @Test
    @DisplayName("Should handle RemoveSuperheroVectorEvent")
    void shouldHandleRemoveSuperheroVectorEvent() {
        Superhero hero = TestStubs.getSuperheroStub();
        RemoveSuperheroVectorEvent event = new RemoveSuperheroVectorEvent(hero);

        subject.handleSuperheroUpdate(event);

        verify(vectorService).removeEmbedding(hero);
    }
}
