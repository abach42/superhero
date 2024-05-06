package com.abach42.superhero.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.abach42.superhero.configuration.TestDataConfiguration;
import com.abach42.superhero.entity.Superhero;

public class SuperheroTest {

    @Test
    @DisplayName("Superhero entity getters get and setters set.")
    public void testGettersAndSettersOfSuperheroEntity() {
        Superhero superhero = TestDataConfiguration.getSuperheroStub();

        assertThat(superhero.getAlias()).isEqualTo("foo");
        assertThat(superhero.getRealName()).isEqualTo("bar");
        assertThat(superhero.getDateOfBirth()).isEqualTo(LocalDate.of(1970, 1, 1));
        assertThat(superhero.getGender()).isEqualTo("Male");
        assertThat(superhero.getOccupation()).isEqualTo("foo");
        assertThat(superhero.getOriginStory()).isEqualTo("foo");

        superhero.setAlias("Superman");
        superhero.setRealName("Clark Kent");
        superhero.setDateOfBirth(LocalDate.of(1938, 6, 1));
        superhero.setGender("Male");
        superhero.setOccupation("Journalist");
        superhero.setOriginStory("Superman was born on the planet Krypton...");

        assertThat(superhero.getAlias()).isEqualTo("Superman");
        assertThat(superhero.getRealName()).isEqualTo("Clark Kent");
        assertThat(superhero.getDateOfBirth()).isEqualTo(LocalDate.of(1938, 6, 1));
        assertThat(superhero.getGender()).isEqualTo("Male");
        assertThat(superhero.getOccupation()).isEqualTo("Journalist");
        assertThat(superhero.getOriginStory()).isEqualTo("Superman was born on the planet Krypton...");
    }
}