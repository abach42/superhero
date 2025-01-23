package com.abach42.superhero.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abach42.superhero.config.api.PathConfig;
import com.abach42.superhero.config.security.SecuredUser;
import com.abach42.superhero.dto.ErrorDto;
import com.abach42.superhero.dto.SkillDto;
import com.abach42.superhero.dto.SkillListDto;
import com.abach42.superhero.exception.ApiException;
import com.abach42.superhero.service.SkillService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Skills")
@RestController
@RequestMapping(path = PathConfig.SKILLS)
@SecurityRequirement(name = "Bearer Authentication")
@SecuredUser
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @Operation(summary = "Get all kills")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "Skills found.",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(       
                        schema = @Schema(
                            implementation = SkillListDto.class)
                    )
                )
            }), 
        @ApiResponse(
            responseCode = "404", 
            description = SkillService.SKILLS_NOT_FOUND_MSG,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    implementation = ErrorDto.class)
            )
        )
    })
    @GetMapping
    public ResponseEntity<SkillListDto> listSkills() throws ApiException {
        SkillListDto skills = skillService.getSkillList();
        return ResponseEntity.ok(skills);
    }

    
    @Operation(summary = "Get skill")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "Skill found.",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        implementation = SkillDto.class)
                )
            }), 
        @ApiResponse( 
            responseCode = "404", 
            description = SkillService.SKILLS_NOT_FOUND_MSG,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    implementation = ErrorDto.class)
            )
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<SkillDto> showSkill(@PathVariable Long id) throws ApiException {
        return ResponseEntity.ok(skillService.getSkillConverted(id));
    }

}
