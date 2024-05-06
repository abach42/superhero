package com.abach42.superhero.unit.entity;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.abach42.superhero.configuration.TestDataConfiguration;
import com.abach42.superhero.entity.Skill;

/*
 * Yes, lombock would do...
 */
public class SkillTest {

    @Test
    @DisplayName("Skill entity getters get and setters set.")
    public void testGettersAndSettersOfSkillEntity() {
        Skill skill = TestDataConfiguration.getSkillStub();

        assertThat(skill.getId()).isEqualTo(1L);
        assertThat(skill.getName()).isEqualTo("foo");

        skill.setName("bar");
        assertThat(skill.getName()).isEqualTo("bar");
    }
}
