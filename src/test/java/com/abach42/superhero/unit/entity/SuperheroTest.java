package com.abach42.superhero.unit.entity;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.abach42.superhero.configuration.TestDataConfiguration;
import com.abach42.superhero.entity.Superhero;

/*
 * Yes, lombock would do...
 */
public class SuperheroTest {
    
    @Test
    @DisplayName("Superhero entity getters get and setters set.")
    public void testGettersAndSettersOfSuperheroEntity() {
        Superhero superhero = TestDataConfiguration.getSuperheroStub();

        assertEquals("foo", superhero.getAlias());
        assertEquals("bar", superhero.getRealName());
        assertEquals(LocalDate.of(1970, 1, 1), superhero.getDateOfBirth());
        assertEquals("Male", superhero.getGender());
        assertEquals("foo", superhero.getOccupation());
        assertEquals("foo", superhero.getOriginStory());

        superhero.setAlias("Superman");
        superhero.setRealName("Clark Kent");
        superhero.setDateOfBirth(LocalDate.of(1938, 6, 1));
        superhero.setGender("Male");
        superhero.setOccupation("Journalist");
        superhero.setOriginStory("Superman was born on the planet Krypton...");

        assertEquals("Superman", superhero.getAlias());
        assertEquals("Clark Kent", superhero.getRealName());
        assertEquals(LocalDate.of(1938, 6, 1), superhero.getDateOfBirth());
        assertEquals("Male", superhero.getGender());
        assertEquals("Journalist", superhero.getOccupation());
        assertEquals("Superman was born on the planet Krypton...", superhero.getOriginStory());
    }
}
