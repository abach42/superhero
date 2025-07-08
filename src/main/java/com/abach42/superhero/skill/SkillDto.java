package com.abach42.superhero.skill;

import com.abach42.superhero.config.validation.OnCreate;
import com.abach42.superhero.config.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

@Schema(name = "skill")
public record SkillDto(
        @Schema(
                title = "skill id",
                example = "1",
                description = "Technical id of database record",
                format = "integer")
        @NotNull(groups = OnCreate.class)
        @Null(groups = OnUpdate.class)
        Long id,

        @Schema(
                title = "skill title",
                example = "good_looks",
                description = "Skill of a superhero",
                accessMode = AccessMode.READ_ONLY)
        @Null(groups = {OnUpdate.class, OnCreate.class})
        @Size(max = 80)
        String name
) {

    public static SkillDto fromDomain(Skill skill) {
        return new SkillDto(
                skill.getId(),
                skill.getName());
    }

    public static Skill toDomain(SkillDto skillDto) {
        return new Skill(
                skillDto.id(),
                skillDto.name()
        );
    }
}