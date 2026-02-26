package com.abach42.superhero.ai.contextual;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TeamRagResponseDtoTest {

    @Test
    @DisplayName("Should hold rag team response fields")
    void shouldHoldFields() {
        TeamRagResponseDto dto = new TeamRagResponseDto(Set.of(1L, 2L), "fits");

        assertThat(dto.team()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(dto.explanation()).isEqualTo("fits");
    }
}
