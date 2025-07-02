package com.abach42.superhero.unit.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.config.api.OnCreate;
import com.abach42.superhero.config.api.OnUpdate;
import com.abach42.superhero.dto.SkillDto;
import com.abach42.superhero.entity.Skill;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SkillDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Skill can be mapped to it's DTO")
    public void testFromDomain() {
        Skill skill = new Skill(1L, "foo");
        SkillDto skillDto = SkillDto.fromDomain(skill);

        assertThat(skillDto.id()).isEqualTo(skill.getId());
        assertThat(skillDto.name()).isEqualTo(skill.getName());
    }

    @Test
    @DisplayName("Skill can be mapped to it's domain")
    public void testToDomain() {
        SkillDto skillDto = new SkillDto(1L, "bar");
        Skill skill = SkillDto.toDomain(skillDto);

        assertThat(skill.getId()).isEqualTo(skillDto.id());
        assertThat(skill.getName()).isEqualTo(skillDto.name());
    }

    @Test
    @DisplayName("Test Validation fails onCreate")
    public void testValidationFailsOnCreate() {
        SkillDto failingSkillDto = new SkillDto(null, "foo");
        Set<ConstraintViolation<SkillDto>> violations = validator.validate(
                failingSkillDto, OnCreate.class);
        assertThat(violations).hasSize(2);
    }

    @Test
    @DisplayName("Test Validation fails onCreate")
    public void testValidationFailsOnUpdate() {
        SkillDto failingSkillDto = new SkillDto(1L, "foo");
        Set<ConstraintViolation<SkillDto>> violations = validator.validate(
                failingSkillDto, OnUpdate.class);
        assertThat(violations).hasSize(2);
    }
}