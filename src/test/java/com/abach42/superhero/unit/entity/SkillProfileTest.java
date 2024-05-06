package com.abach42.superhero.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.abach42.superhero.entity.Skill;
import com.abach42.superhero.entity.SkillProfile;

class SkillProfileTest {

    @Test
    @DisplayName("Test constructor and getter")
    void constructorAndGettersWorkCorrectly() {
        Long superheroId = 1L;
        Integer intensity = 5;
        Skill skill = new Skill("foo");

        SkillProfile skillProfile = new SkillProfile(superheroId, intensity, skill);

        assertThat(skillProfile.getSuperheroId()).isEqualTo(superheroId);
        assertThat(skillProfile.getIntensity()).isEqualTo(intensity);
        assertThat(skillProfile.getSkill()).isEqualTo(skill);
    }

    @Test
    
    @DisplayName("Test setter")
    void settersWorkCorrectly() {
        SkillProfile skillProfile = new SkillProfile(1L, 5, new Skill("foo"));
        Long newSuperheroId = 2L;
        Integer newIntensity = 3;
        Skill newSkill = new Skill("bar");

        skillProfile.setSuperheroId(newSuperheroId);
        skillProfile.setIntensity(newIntensity);
        skillProfile.setSkill(newSkill);

        assertThat(skillProfile.getSuperheroId()).isEqualTo(newSuperheroId);
        assertThat(skillProfile.getIntensity()).isEqualTo(newIntensity);
        assertThat(skillProfile.getSkill()).isEqualTo(newSkill);
    }
}