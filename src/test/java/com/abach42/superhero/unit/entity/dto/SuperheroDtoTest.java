package com.abach42.superhero.unit.entity.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.abach42.superhero.config.OnCreate;
import com.abach42.superhero.config.OnUpdate;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.dto.SuperheroDto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

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

    @Test
    @DisplayName("Suphero DTO validates not null constraints")
    public void testValidationOnCreateNotNull() {
        SuperheroDto fialingSuperheroDto = new SuperheroDto(null, null, null, null, null, null);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<SuperheroDto>> constraintViolations = validator.validate(
            fialingSuperheroDto, OnCreate.class
        );
        assertThat(constraintViolations).hasSize(4);
    }

    @Test
    @DisplayName("Suphero DTO validation allows null")
    public void testValidationOnUpdateNull() {
        SuperheroDto fialingSuperheroDto = new SuperheroDto(null, null, null, null, null, null);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<SuperheroDto>> constraintViolations = validator.validate(
            fialingSuperheroDto, OnUpdate.class
        );
        assertThat(constraintViolations).hasSize(0);
    }
}