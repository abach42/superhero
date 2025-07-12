package com.abach42.superhero.superhero;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.abach42.superhero.config.validation.OnCreate;
import com.abach42.superhero.config.validation.OnUpdate;
import com.abach42.superhero.testconfiguration.TestStubs;
import com.abach42.superhero.user.ApplicationUserDto;
import com.abach42.superhero.user.UserRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags(value = {@Tag("unit"), @Tag("superhero")})
public class SuperheroDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Superhero can be mapped to it's DTO")
    public void testFromDomain() {
        Superhero superhero = TestStubs.getSuperheroStub();
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
        ApplicationUserDto userDto = new ApplicationUserDto("username", "password", UserRole.USER);
        SuperheroDto superheroDto = new SuperheroDto(null, "Batman", "Bruce Wayne",
                LocalDate.of(1939, 5, 1), Gender.MALE,
                "Businessman", "The Dark Knight", userDto);

        Superhero superhero = SuperheroDto.toDomain(superheroDto);

        assertEquals(superheroDto.alias(), superhero.getAlias());
        assertEquals(superheroDto.realName(), superhero.getRealName());
        assertEquals(superheroDto.dateOfBirth(), superhero.getDateOfBirth());
        assertEquals(superheroDto.gender(), superhero.getGender());
        assertEquals(superheroDto.occupation(), superhero.getOccupation());
        assertEquals(superheroDto.originStory(), superhero.getOriginStory());
        assertEquals(superheroDto.user().role(), superhero.getUser().getRole());
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
        ApplicationUserDto userDto = new ApplicationUserDto("username", "password", UserRole.USER);
        SuperheroDto failingSuperheroDto = new SuperheroDto(777L,
                "abcdefghijklmnopqrstu".substring(0, 21), "foo", LocalDate.of(1970, 1, 1),
                Gender.NOT_PROVIDED, "ignore", "ignore", userDto);

        Set<ConstraintViolation<SuperheroDto>> constraintViolations = validator.validate(
                failingSuperheroDto, OnCreate.class, OnUpdate.class);
        assertThat(constraintViolations).hasSize(1);
    }
}