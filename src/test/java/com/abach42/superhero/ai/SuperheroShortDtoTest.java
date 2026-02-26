package com.abach42.superhero.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.testconfiguration.TestStubs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SuperheroShortDtoTest {

    @Test
    @DisplayName("Should map superhero domain object to short dto")
    void shouldMapFromDomain() {
        var hero = TestStubs.getSuperheroStub();

        SuperheroShortDto dto = SuperheroShortDto.fromDomain(hero);

        assertThat(dto.alias()).isEqualTo(hero.getAlias());
        assertThat(dto.realName()).isEqualTo(hero.getRealName());
        assertThat(dto.occupation()).isEqualTo(hero.getOccupation());
    }
}
