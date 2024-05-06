package com.abach42.superhero.unit.dto;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.abach42.superhero.config.api.OnCreate;
import com.abach42.superhero.config.api.OnUpdate;
import com.abach42.superhero.dto.SkillDto;
import com.abach42.superhero.dto.SkillProfileDto;
import com.abach42.superhero.entity.Skill;
import com.abach42.superhero.entity.SkillProfile;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class SkillProfileDtoTest {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Skill can be mapped to it's DTO")
    public void testFromDomain() {
        Skill skill = new Skill(1L, "foo");
        SkillProfile skillProfile = new SkillProfile(1L, 2, skill);
        SkillProfileDto skillProfileDto = SkillProfileDto.fromDomain(skillProfile);

        assertThat(skillProfileDto.id()).isEqualTo(skillProfile.getId());
        assertThat(skillProfileDto.superheroId()).isEqualTo(skillProfile.getSuperheroId());
        assertThat(skillProfileDto.intensity()).isEqualTo(skillProfile.getIntensity());
        assertThat(skillProfileDto.skill()).usingRecursiveComparison().isEqualTo(skillProfile.getSkill());
    }

    @Test
    @DisplayName("SkillProfileDto can be mapped to its domain object")
    public void testToDomain() {
        SkillDto skillDto = new SkillDto(1L, "foo");
        SkillProfileDto skillProfileDto = new SkillProfileDto(1L, 2L, 3, skillDto);
    
        SkillProfile skillProfile = SkillProfileDto.toDomain(skillProfileDto);
    
        assertThat(skillProfile.getSuperheroId()).isEqualTo(skillProfileDto.superheroId());
        assertThat(skillProfile.getIntensity()).isEqualTo(skillProfileDto.intensity());
        assertThat(skillProfile.getSkill().getId()).isEqualTo(skillProfileDto.skill().id());
        assertThat(skillProfile.getSkill().getName()).isEqualTo(skillProfileDto.skill().name());
    }

    @Test
    @DisplayName("Test Validation fails onCreate")
    public void testValidationFailsOnCreate() {
        SkillProfileDto failingSkillProfileDto = new SkillProfileDto(1L, 2L, null, null);
    
        Set<ConstraintViolation<SkillProfileDto>> violations = validator.validate(
            failingSkillProfileDto, OnCreate.class);

        assertThat(violations).hasSize(4);
    }

    @Test
    @DisplayName("Test Validation fails onCreate")
    public void testValidationFailsOnUpdate() {
        SkillDto skillDto = new SkillDto(1L, "foo");
        SkillProfileDto failingSkillProfileDto = new SkillProfileDto(1L, 2L, null, skillDto);

        Set<ConstraintViolation<SkillProfileDto>> violations = validator.validate(
            failingSkillProfileDto, OnUpdate.class);

        assertThat(violations).hasSize(6);
    }
}
