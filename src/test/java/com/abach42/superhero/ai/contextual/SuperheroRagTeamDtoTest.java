package com.abach42.superhero.ai.contextual;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.testconfiguration.TestStubs;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SuperheroRagTeamDtoTest {

    @Test
    @DisplayName("Should hold contextual team dto fields")
    void shouldHoldFields() {
        SuperheroRagTeamDto dto = new SuperheroRagTeamDto("rescue",
                List.of(TestStubs.getSuperheroShortDtoStub()), "why");

        assertThat(dto.taskDescription()).isEqualTo("rescue");
        assertThat(dto.members()).hasSize(1);
        assertThat(dto.explanation()).isEqualTo("why");
    }
}
