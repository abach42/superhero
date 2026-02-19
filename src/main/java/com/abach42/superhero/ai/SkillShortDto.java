package com.abach42.superhero.ai;

import com.abach42.superhero.shared.validation.OnCreate;
import com.abach42.superhero.shared.validation.OnUpdate;
import com.abach42.superhero.skillprofile.SkillProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record SkillShortDto(
        @Schema(
                title = "skill title",
                example = "good_looks",
                description = "Skill of a superhero",
                accessMode = AccessMode.READ_ONLY)
        @Null(groups = {OnUpdate.class, OnCreate.class})
        @Size(max = 80)
        String name,

        @Schema(
                title = "intensity",
                example = "1",
                description = "Intensity of skill 1 - 5",
                minimum = "1",
                maximum = "5",
                format = "integer"
        )
        @NotNull(groups = {OnCreate.class, OnUpdate.class})
        Integer intensity) {

    public static SkillShortDto fromDomain(SkillProfile skillProfile) {
        return new SkillShortDto(skillProfile.getSkill().getName(), skillProfile.getIntensity());
    }
}
