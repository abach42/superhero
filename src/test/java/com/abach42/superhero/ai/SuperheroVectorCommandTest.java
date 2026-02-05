package com.abach42.superhero.ai;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SuperheroVectorCommandTest {

    @Mock
    private VectorService vectorService;

    private SuperheroVectorCommand subject;

    @BeforeEach
    void setUp() {
        subject = new SuperheroVectorCommand(vectorService);
    }

    @Test
    @DisplayName("Should call updateSuperheroes on command execution")
    void shouldCallUpdateSuperheroes() {
        subject.updateSemanticData();

        verify(vectorService).updateSuperheroes();
    }
}
