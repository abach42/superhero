package com.abach42.superhero.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@Schema(name = "skills")
public class SkillListDto {
    @Schema(
        title = "skills", 
        description = "List of skills", 
        format = "array", 
        accessMode = AccessMode.READ_ONLY
    )
    private final List<SkillDto> skills;

    public SkillListDto() {
        this.skills = null;
    }

    public SkillListDto(List<SkillDto> skills) {
        this.skills = skills;
    }

    public List<SkillDto> getSkills() {
        return skills;
    }
}