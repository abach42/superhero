package com.abach42.superhero.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.testconfiguration.TestStubs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RemoveSuperheroVectorEventTest {

    @Test
    @DisplayName("Should store superhero in remove vector event")
    void shouldStoreSuperhero() {
        var hero = TestStubs.getSuperheroStub();

        RemoveSuperheroVectorEvent event = new RemoveSuperheroVectorEvent(hero);

        assertThat(event.superhero()).isSameAs(hero);
    }
}
