package com.abach42.superhero.skill;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.util.List;

@Schema(name = "skills")
public record SkillListDto(
        @Schema(
                title = "skills",
                description = "List of skills",
                format = "array",
                accessMode = AccessMode.READ_ONLY
        )
        List<SkillDto> skills) {

}