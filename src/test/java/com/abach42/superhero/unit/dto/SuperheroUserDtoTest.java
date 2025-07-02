package com.abach42.superhero.unit.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.abach42.superhero.config.api.OnCreate;
import com.abach42.superhero.config.api.OnUpdate;
import com.abach42.superhero.dto.SuperheroUserDto;
import com.abach42.superhero.entity.SuperheroUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SuperheroUserDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Skill can be mapped to it's DTO")
    public void testFromDomain() {
        SuperheroUser superheroUser = new SuperheroUser("foo@bar.org", "password", "USER");

        SuperheroUserDto userDto = SuperheroUserDto.fromDomain(superheroUser);

        assertThat(superheroUser.getEmail()).isEqualTo(userDto.email());
        assertThat(superheroUser.getPassword()).isEqualTo(userDto.password());
        assertThat("USER").isEqualTo(userDto.role());
    }

    @Test
    @DisplayName("Skill can be mapped to it's domain")
    public void testToDomain() {
        SuperheroUserDto userDto = new SuperheroUserDto("foo@bar.org", "password", "USER");

        SuperheroUser superheroUser = SuperheroUserDto.toDomain(userDto);

        assertThat(userDto.email()).isEqualTo(superheroUser.getEmail());
        assertThat(userDto.password()).isEqualTo(superheroUser.getPassword());
        assertThat("USER").isEqualTo(superheroUser.getRole());
    }

    @Test
    void getRoleFromStringReturnsCorrectRole() {
        assertThat("USER").isEqualTo(SuperheroUserDto.RoleEnum.getFromString("user"));
        assertThat("ADMIN").isEqualTo(SuperheroUserDto.RoleEnum.getFromString("admin"));
    }

    @Test
    void getRoleFromStringThrowsExceptionForInvalidRole() {
        assertThrows(IllegalArgumentException.class,
                () -> SuperheroUserDto.RoleEnum.getFromString("invalid_role"));
    }

    @Test
    @DisplayName("Test Validation fails onCreate")
    public void testValidationFailsOnCreate() {
        SuperheroUserDto failingUserDto = new SuperheroUserDto(null, null, null);
        Set<ConstraintViolation<SuperheroUserDto>> violations = validator.validate(
                failingUserDto, OnCreate.class);
        assertThat(violations).hasSize(3);
    }

    @Test
    @DisplayName("Test Validation fails onCreate")
    public void testValidationFailsOnUpdate() {
        SuperheroUserDto failingUserDto = new SuperheroUserDto("foo", "bar", "baz");
        Set<ConstraintViolation<SuperheroUserDto>> violations = validator.validate(
                failingUserDto, OnUpdate.class);
        assertThat(violations).hasSize(0);
    }

    @Test
    @DisplayName("Test password can be written, but not be read 🦉")
    public void testPasswordIsHidden() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SuperheroUserDto failingUserDto = new SuperheroUserDto("foo",
                "this password is written but not read", "bar");
        String json = mapper.writeValueAsString(failingUserDto);
        SuperheroUserDto readUser = mapper.readValue(json, SuperheroUserDto.class);

        assertThat(readUser.password()).isNull();
    }
}
