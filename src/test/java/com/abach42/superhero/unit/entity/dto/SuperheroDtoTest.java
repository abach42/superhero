package com.abach42.superhero.unit.entity.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.dto.SuperheroDto;

public class SuperheroDtoTest {
    @Test
    @DisplayName("Superhero can be mappted to it's DTO")
    public void testFromDomain() {
        Superhero superhero = new Superhero("Superman", "Clark Joseph Kent", LocalDate.of(1970, 1, 1), "Male",
                "Journalist", "Some story");

        SuperheroDto superheroDto = SuperheroDto.fromDomain(superhero);
        
        assertEquals(superhero.getId(), superheroDto.id());
        assertEquals(superhero.getAlias(), superheroDto.alias());
        assertEquals(superhero.getDateOfBirth(), superheroDto.dateOfBirth());
        assertEquals(superhero.getGender(), superheroDto.gender());
        assertEquals(superhero.getOccupation(), superheroDto.occupation());
    }
}