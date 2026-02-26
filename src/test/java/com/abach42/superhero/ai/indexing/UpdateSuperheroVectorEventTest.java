package com.abach42.superhero.ai.indexing;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.testconfiguration.TestStubs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateSuperheroVectorEventTest {

    @Test
    @DisplayName("Should store superhero in update vector event")
    void shouldStoreSuperhero() {
        var hero = TestStubs.getSuperheroStub();

        UpdateSuperheroVectorEvent event = new UpdateSuperheroVectorEvent(hero);

        assertThat(event.superhero()).isSameAs(hero);
    }
}
