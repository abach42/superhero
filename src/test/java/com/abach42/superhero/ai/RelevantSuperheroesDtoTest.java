package com.abach42.superhero.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.testconfiguration.TestStubs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RelevantSuperheroesDtoTest {

    @Test
    @DisplayName("Should map SemanticMatch to RelevantSuperheroesDto")
    void shouldMapFromSemanticMatch() {
        var hero = TestStubs.getSuperheroStub();
        SemanticMatch match = new SemanticMatch(hero, 0.77);

        RelevantSuperheroesDto dto = RelevantSuperheroesDto.fromSemanticMatch(match);

        assertThat(dto.relevance()).isEqualTo(0.77);
        assertThat(dto.superhero().alias()).isEqualTo(hero.getAlias());
        assertThat(dto.superhero().realName()).isEqualTo(hero.getRealName());
    }
}
