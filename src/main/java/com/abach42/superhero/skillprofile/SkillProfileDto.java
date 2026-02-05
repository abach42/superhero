package com.abach42.superhero.skillprofile;

import com.abach42.superhero.shared.validation.OnCreate;
import com.abach42.superhero.shared.validation.OnUpdate;
import com.abach42.superhero.skill.SkillDto;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

@Schema(name = "skill profile")
public record SkillProfileDto(
        @Schema(
                title = "skill profile id",
                example = "1",
                description = "Technical id of database record",
                format = "integer",
                accessMode = AccessMode.READ_ONLY
        )
        @Null(groups = {OnCreate.class, OnUpdate.class})
        Long id,

        @Schema(
                title = "superhero id",
                example = "1",
                description = "Technical id of superhero record",
                format = "integer",
                accessMode = AccessMode.READ_ONLY
        )
        @Null(groups = {OnCreate.class, OnUpdate.class})
        Long superheroId,

        @Schema(
                title = "intensity",
                example = "1",
                description = "Intensity of skill 1 - 5",
                minimum = "1",
                maximum = "5",
                format = "integer"
        )
        @NotNull(groups = {OnCreate.class, OnUpdate.class})
        Integer intensity,

        @NotNull(groups = OnCreate.class)
        @Null(groups = OnUpdate.class)
        @Valid
        SkillDto skill
) {

    public static SkillProfileDto fromDomain(SkillProfile skillProfile) {
        return new SkillProfileDto(
                skillProfile.getId(),
                skillProfile.getSuperheroId(),
                skillProfile.getIntensity(),
                SkillDto.fromDomain(skillProfile.getSkill()));
    }

    public static SkillProfile toDomain(SkillProfileDto skillProfileDto) {
        return new SkillProfile(
                skillProfileDto.superheroId(),
                skillProfileDto.intensity(),
                SkillDto.toDomain(skillProfileDto.skill));
    }

    //workaround waiting for io.swagger.v3.oas using groups
    @Schema(name = SkillProfileSwaggerPut.SKILL_PROFILE_PUT)
    public record SkillProfileSwaggerPut(Integer intensity) {

        public static final String SKILL_PROFILE_PUT = "skill profile put";
    }
}