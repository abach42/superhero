package com.abach42.superhero.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.skillprofile.SkillProfile;
import com.abach42.superhero.testconfiguration.TestStubs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SkillShortDtoTest {

    @Test
    @DisplayName("Should map SkillProfile to SkillShortDto")
    void shouldMapSkillProfileToSkillShortDto() {
        SkillProfile skillProfile = TestStubs.getSkillProfileStub();

        SkillShortDto result = SkillShortDto.fromDomain(skillProfile);

        assertThat(result.name()).isEqualTo(skillProfile.getSkill().getName());
        assertThat(result.intensity()).isEqualTo(skillProfile.getIntensity());
    }
}
