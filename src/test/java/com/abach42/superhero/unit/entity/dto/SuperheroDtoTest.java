package com.abach42.superhero.unit.entity.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.abach42.superhero.config.api.OnCreate;
import com.abach42.superhero.config.api.OnUpdate;
import com.abach42.superhero.configuration.TestDataConfiguration;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.dto.SuperheroDto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class SuperheroDtoTest {
    private SuperheroDto failingSuperheroDto;

    @BeforeEach
    public void setUp() {
        this.failingSuperheroDto = TestDataConfiguration.getSuperheroDtoStubEmpty();
    }
    
    @Test
    @DisplayName("Superhero can be mappted to it's DTO")
    public void testFromDomain() {
        Superhero superhero = TestDataConfiguration.getSuperheroStub();
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
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<SuperheroDto>> constraintViolations = validator.validate(
                failingSuperheroDto, OnCreate.class);
        assertThat(constraintViolations).hasSize(4);
    }

    @Test
    @DisplayName("Suphero DTO validation allows null")
    public void testValidationOnUpdateNull() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<SuperheroDto>> constraintViolations = validator.validate(
                failingSuperheroDto, OnUpdate.class);
        assertThat(constraintViolations).hasSize(0);
    }
}