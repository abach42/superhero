package com.abach42.superhero.ai.indexing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VectorExceptionTest {

    @Test
    @DisplayName("Should keep vector exception message")
    void shouldKeepMessage() {
        VectorException exception = new VectorException("vector failed");

        assertThat(exception).hasMessage("vector failed");
    }
}
