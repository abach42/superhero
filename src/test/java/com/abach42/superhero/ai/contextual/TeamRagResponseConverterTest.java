package com.abach42.superhero.ai.contextual;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TeamRagResponseConverterTest {

    @Test
    @DisplayName("Should create bean output converter for team rag response")
    void shouldCreateOutputConverter() {
        TeamRagResponseConverter subject = new TeamRagResponseConverter();

        var converter = subject.outputConverter();

        TeamRagResponseDto dto = converter.convert("{\"team\":[1,2],\"explanation\":\"ok\"}");
        assertThat(dto.team()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(dto.explanation()).isEqualTo("ok");
    }
}
