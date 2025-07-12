package com.abach42.superhero.superhero;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.skillprofile.SkillProfile;
import com.abach42.superhero.testconfiguration.TestStubs;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags(value = {@Tag("unit"), @Tag("superhero")})
public class SuperheroTest {

    @Test
    @DisplayName("Superhero entity getters get and setters set.")
    public void testGettersAndSettersOfSuperheroEntity() {
        Superhero superhero = TestStubs.getSuperheroStub();

        assertThat(superhero.getAlias()).isEqualTo("foo");
        assertThat(superhero.getRealName()).isEqualTo("bar");
        assertThat(superhero.getDateOfBirth()).isEqualTo(
                LocalDate.of(1970, 1, 1));
        assertThat(superhero.getGender()).isEqualTo(Gender.MALE);
        assertThat(superhero.getOccupation()).isEqualTo("foo");
        assertThat(superhero.getOriginStory()).isEqualTo("foo");

        superhero.setAlias("Superman");
        superhero.setRealName("Clark Kent");
        superhero.setDateOfBirth(LocalDate.of(1938, 6, 1));
        superhero.setGender(Gender.MALE);
        superhero.setOccupation("Journalist");
        superhero.setOriginStory("Superman was born on the planet Krypton...");

        assertThat(superhero.getAlias()).isEqualTo("Superman");
        assertThat(superhero.getRealName()).isEqualTo("Clark Kent");
        assertThat(superhero.getDateOfBirth()).isEqualTo(LocalDate.of(
                1938, 6, 1));
        assertThat(superhero.getGender()).isEqualTo(Gender.MALE);
        assertThat(superhero.getOccupation()).isEqualTo("Journalist");
        assertThat(superhero.getOriginStory()).isEqualTo(
                "Superman was born on the planet Krypton...");
    }

    @Test
    @DisplayName("Superhero entity deleted field getters and setters work correctly")
    public void testDeletedFieldGettersAndSetters() {
        Superhero superhero = TestStubs.getSuperheroStub();

        assertThat(superhero.getDeleted()).isFalse();

        superhero.setDeleted(true);
        assertThat(superhero.getDeleted()).isTrue();

        superhero.setDeleted(false);
        assertThat(superhero.getDeleted()).isFalse();
    }

    @Test
    @DisplayName("Superhero entity skill profiles getters and setters work correctly")
    public void testSkillProfilesGettersAndSetters() {
        Superhero superhero = TestStubs.getSuperheroStub();

        assertThat(superhero.getSkillProfiles()).isNotNull();
        assertThat(superhero.getSkillProfiles()).isEmpty();

        List<SkillProfile> skillProfiles = new ArrayList<>();
        skillProfiles.add(TestStubs.getSkillProfileStub());

        superhero.setSkillProfiles(skillProfiles);
        assertThat(superhero.getSkillProfiles()).isEqualTo(skillProfiles);
        assertThat(superhero.getSkillProfiles()).hasSize(1);

        List<SkillProfile> newSkillProfiles = new ArrayList<>();
        newSkillProfiles.add(TestStubs.getSkillProfileStub());
        newSkillProfiles.add(TestStubs.getSkillProfileToCreateStub());

        superhero.setSkillProfiles(newSkillProfiles);
        assertThat(superhero.getSkillProfiles()).isEqualTo(newSkillProfiles);
        assertThat(superhero.getSkillProfiles()).hasSize(2);
    }
}