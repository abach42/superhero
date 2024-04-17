package com.abach42.superhero.unit;

import org.junit.jupiter.api.Test;

import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.dto.SuperheroDto;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SuperheroDtoTest {
    @Test
    public void testFromDomain() {
        // Arrange
        Superhero superhero = new Superhero("Superman", "Clark Joseph Kent", LocalDate.of(1970, 1, 1), "Male", "Journalist", "Some story");

        // Act
        SuperheroDto superheroDto = SuperheroDto.fromDomain(superhero);

        // Assert
        assertEquals(superhero.getId(), superheroDto.id());
        assertEquals(superhero.getAlias(), superheroDto.alias());
        assertEquals(superhero.getDateOfBirth(), superheroDto.dateOfBirth());
        assertEquals(superhero.getGender(), superheroDto.gender());
        assertEquals(superhero.getOccupation(), superheroDto.occupation());
    }
}