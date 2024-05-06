package com.abach42.superhero.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@Schema(name = "skill profiles")
public record SkillProfileListDto(
    @Schema(
        title = "skill profiles", 
        description = "List of skill profiles of a superhero", 
        format = "array", 
        accessMode = AccessMode.READ_ONLY
    )
    List<SkillProfileDto> skillprofiles
) {

}
