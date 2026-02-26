package com.abach42.superhero.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SemanticSearchExceptionTest {

    @Test
    @DisplayName("Should keep exception message")
    void shouldKeepMessage() {
        RuntimeException cause = new RuntimeException("root");

        SemanticSearchException exception = new SemanticSearchException("semantic failed", cause);

        assertThat(exception).hasMessage("semantic failed");
    }
}
