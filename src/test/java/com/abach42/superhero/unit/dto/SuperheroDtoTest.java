package com.abach42.superhero.unit.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.abach42.superhero.config.api.OnCreate;
import com.abach42.superhero.config.api.OnUpdate;
import com.abach42.superhero.configuration.TestDataConfiguration;
import com.abach42.superhero.dto.SuperheroDto;
import com.abach42.superhero.dto.SuperheroUserDto;
import com.abach42.superhero.entity.Superhero;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SuperheroDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Superhero can be mapped to it's DTO")
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
    @DisplayName("SuperheroDto can be mapped to its domain object")
    public void testToDomain() {
        SuperheroUserDto userDto = new SuperheroUserDto("username", "password", "ROLE");
        SuperheroDto superheroDto = new SuperheroDto(null, "Batman", "Bruce Wayne",
                LocalDate.of(1939, 5, 1), "Male",
                "Businessman", "The Dark Knight", userDto);

        Superhero superhero = SuperheroDto.toDomain(superheroDto);

        assertEquals(superheroDto.alias(), superhero.getAlias());
        assertEquals(superheroDto.realName(), superhero.getRealName());
        assertEquals(superheroDto.dateOfBirth(), superhero.getDateOfBirth());
        assertEquals(superheroDto.gender(), superhero.getGender());
        assertEquals(superheroDto.occupation(), superhero.getOccupation());
        assertEquals(superheroDto.originStory(), superhero.getOriginStory());
    }

    @Test
    @DisplayName("Test Validation fails onCreate")
    public void testValidationOnCreateNotNull() {
        SuperheroDto failingSuperheroDto = new SuperheroDto(null, null, null, null,
                null, "ignore", "ignore", null);

        Set<ConstraintViolation<SuperheroDto>> constraintViolations = validator.validate(
                failingSuperheroDto, OnCreate.class);
        assertThat(constraintViolations).hasSize(5);
    }

    @Test
    @DisplayName("Test Validation fails onCreate")
    public void testValidationOnUpdateNull() {
        SuperheroDto failingSuperheroDto = new SuperheroDto(777L, null, null, null,
                null, "ignore", "ignore", null);

        Set<ConstraintViolation<SuperheroDto>> constraintViolations = validator.validate(
                failingSuperheroDto, OnUpdate.class);
        assertThat(constraintViolations).hasSize(0);
    }

    @Test
    @DisplayName("Test Validation alias size onCreate/ onUpdate")
    public void testValidationAliasSize() {
        SuperheroUserDto userDto = new SuperheroUserDto("username", "password", "ROLE");
        SuperheroDto failingSuperheroDto = new SuperheroDto(777L,
                "abcdefghijklmnopqrstu".substring(0, 21), "foo", LocalDate.of(1970, 1, 1),
                "foo", "ignore", "ignore", userDto);

        Set<ConstraintViolation<SuperheroDto>> constraintViolations = validator.validate(
                failingSuperheroDto, OnCreate.class, OnUpdate.class);
        assertThat(constraintViolations).hasSize(1);
    }
}