package com.abach42.superhero.skillprofile;

import com.abach42.superhero.shared.api.ApiException;
import com.abach42.superhero.shared.api.ErrorDetailedDto;
import com.abach42.superhero.shared.api.ErrorDto;
import com.abach42.superhero.shared.api.PathConfig;
import com.abach42.superhero.shared.validation.OnCreate;
import com.abach42.superhero.shared.validation.OnUpdate;
import com.abach42.superhero.login.methodsecurity.IsAdmin;
import com.abach42.superhero.login.methodsecurity.IsUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Skill Profile")
@RestController
@RequestMapping(path = PathConfig.SKILL_PROFILES)
@SecurityRequirement(name = "Bearer Authentication")
@IsAdmin
public class SkillProfileController {

    private final SkillProfileService skillProfileService;

    public SkillProfileController(SkillProfileService skillProfileService) {
        this.skillProfileService = skillProfileService;
    }

    @Operation(summary = "Get all skill profiles of a superhero")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Skill profiles found.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = SkillProfileListDto.class)
                                    )
                            )
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = SkillProfileService.SKILL_PROFILES_SUPERHERO_NOT_FOUND_MSG + "1",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            )
    })
    @IsUser
    @GetMapping
    public ResponseEntity<SkillProfileListDto> listSuperheroSkillProfiles(
            @PathVariable Long superheroId)
            throws ApiException {
        SkillProfileListDto skillProfiles = skillProfileService.retrieveSuperheroSkillProfileList(
                superheroId);
        return ResponseEntity.ok(skillProfiles);
    }

    @Operation(summary = "Get skill profile of a superhero")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Skill profile found.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SkillProfileDto.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = SkillProfileService.SKILL_PROFILE_SUPERHERO_NOT_FOUND_MSG,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorDto.class)
                    )
            )
    })
    @IsUser
    @GetMapping("/{skillId}")
    public ResponseEntity<SkillProfileDto> showSuperheroSkillProfile(@PathVariable Long superheroId,
            @PathVariable Long skillId) throws ApiException {
        SkillProfileDto skillProfileDto = skillProfileService.retrieveSuperheroSkillProfile(
                superheroId, skillId);
        return ResponseEntity.ok(skillProfileDto);
    }

    @Operation(summary = "Add new skill to profile of superhero")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Superhero skill profile created.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SkillProfileDto.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid request content.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorDetailedDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = SkillProfileService.SKILL_PROFILE_SUPERHERO_NOT_CREATED_MSG,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access Denied, when false user role",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorDto.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<SkillProfileDto> createSuperheroSkillProfile(
            @PathVariable Long superheroId,
            @Validated(OnCreate.class) @RequestBody SkillProfileDto skillProfileDto)
            throws ApiException {
        SkillProfileDto createdSkillProfileDto = skillProfileService.addSuperheroSkillProfile(
                superheroId, skillProfileDto);
        URI uri = UriComponentsBuilder.fromUriString(PathConfig.SKILL_PROFILES + "/{id}")
                .build(superheroId, createdSkillProfileDto.id());
        return ResponseEntity.created(uri).body(createdSkillProfileDto);
    }

    @Operation(summary = "Update existing skill to profile of superhero")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Superhero skill profile updated",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SkillProfileDto.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = SkillProfileService.SKILL_PROFILE_SUPERHERO_NOT_FOUND_MSG,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = SkillProfileService.SKILL_PROFILE_SUPERHERO_NOT_UPDATED_MSG,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access Denied, when false user role",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorDto.class)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            // workaround waiting for io.swagger.v3.oas using groups
                            ref = SkillProfileDto.SkillProfileSwaggerPut.SKILL_PROFILE_PUT,
                            example = "1"
                    )
            )
    )
    @PatchMapping("/{skillId}")
    public ResponseEntity<SkillProfileDto> updateSuperheroSkillProfile(
            @PathVariable Long superheroId,
            @PathVariable Long skillId,
            @Validated(OnUpdate.class) @RequestBody SkillProfileDto skillProfileDto)
            throws ApiException {
        SkillProfileDto updatedSkillProfileDto = skillProfileService.changeSuperheroSkillProfile(
                superheroId, skillId,
                skillProfileDto);
        return ResponseEntity.ok(updatedSkillProfileDto);
    }

    @Operation(summary = "Delete skill profile of a superhero (erase record)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Skill profile of a superhero",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SkillProfileDto.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = SkillProfileService.SKILL_PROFILE_SUPERHERO_NOT_FOUND_MSG,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access Denied, when false user role",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorDto.class)
                    )
            )
    })
    @DeleteMapping("/{skillId}")
    public ResponseEntity<SkillProfileDto> deleteSuperheroSkillProfile(
            @PathVariable Long superheroId,
            @PathVariable Long skillId) {
        SkillProfileDto deleted = skillProfileService.deleteSuperheroSkillProfile(superheroId,
                skillId);
        return ResponseEntity.ok(deleted);
    }
}