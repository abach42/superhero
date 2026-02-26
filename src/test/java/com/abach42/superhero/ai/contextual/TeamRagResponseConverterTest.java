package com.abach42.superhero.ai.contextual;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TeamRagResponseConverterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should create bean output converter for team rag response")
    void shouldCreateOutputConverter() throws JsonProcessingException {
        TeamRagResponseConverter subject = new TeamRagResponseConverter();

        var converter = subject.outputConverter();

        TeamRagResponseDto dto = converter.convert(objectMapper.writeValueAsString(
                new TeamRagResponseDto(Set.of(1L, 2L), "ok")));
        assertThat(dto.team()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(dto.explanation()).isEqualTo("ok");
    }
}
