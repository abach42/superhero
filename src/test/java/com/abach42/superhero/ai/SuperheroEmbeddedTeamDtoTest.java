package com.abach42.superhero.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.testconfiguration.TestStubs;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SuperheroEmbeddedTeamDtoTest {

    @Test
    @DisplayName("Should convert semantic matches into embedded team dto")
    void shouldConvertSemanticMatches() {
        String task = "Rescue city";
        SemanticMatch match = new SemanticMatch(TestStubs.getSuperheroStub(), 0.95);

        SuperheroEmbeddedTeamDto dto = SuperheroEmbeddedTeamDto.fromSemanticMatches(task, List.of(match));

        assertThat(dto.taskDescription()).isEqualTo(task);
        assertThat(dto.members()).hasSize(1);
        assertThat(dto.members().get(0).relevance()).isEqualTo(0.95);
    }
}
