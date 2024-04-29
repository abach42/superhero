package com.abach42.superhero.unit.entity;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.SuperheroUser;

/*
 * Yes, lombock would do...
 */
public class SuperheroTest {
    
    @Test
    @DisplayName("Superhero entity getters get and setters set.")
    public void testGettersAndSettersOfSuperheroEntity() {
        SuperheroUser user = new SuperheroUser("foo", "bar", "USER");

        Superhero superhero = new Superhero(
                "Batman",
                "Bruce Wayne",
                LocalDate.of(1939, 5, 1),
                "Male",
                "Crimefighter",
                "After witnessing the murder of his parents...", 
                user);

        assertEquals("Batman", superhero.getAlias());
        assertEquals("Bruce Wayne", superhero.getRealName());
        assertEquals(LocalDate.of(1939, 5, 1), superhero.getDateOfBirth());
        assertEquals("Male", superhero.getGender());
        assertEquals("Crimefighter", superhero.getOccupation());
        assertEquals("After witnessing the murder of his parents...", superhero.getOriginStory());

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
