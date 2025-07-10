package com.abach42.superhero.skill;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.testconfiguration.TestStubs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags(value = {@Tag("unit"), @Tag("skill")})
public class SkillTest {

    @Test
    @DisplayName("Skill entity getters get and setters set.")
    public void testGettersAndSettersOfSkillEntity() {
        Skill skill = TestStubs.getSkillStub();

        assertThat(skill.getId()).isEqualTo(1L);
        assertThat(skill.getName()).isEqualTo("foo");

        skill.setName("bar");
        assertThat(skill.getName()).isEqualTo("bar");
    }
}
