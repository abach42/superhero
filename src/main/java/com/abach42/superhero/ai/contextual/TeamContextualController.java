package com.abach42.superhero.ai.contextual;

import com.abach42.superhero.ai.SuperheroEmbeddedTeamDto;
import com.abach42.superhero.login.methodsecurity.IsAdmin;
import com.abach42.superhero.login.methodsecurity.IsUser;
import com.abach42.superhero.shared.api.ErrorDto;
import com.abach42.superhero.shared.api.PathConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Collect a team by contextual research.")
@RestController
@RequestMapping(path = PathConfig.SUPERHEROES)
@SecurityRequirement(name = "Bearer Authentication")
@IsUser
public class TeamContextualController {

    private final TeamContextualService teamContextualService;

    public TeamContextualController(TeamContextualService teamContextualService) {
        this.teamContextualService = teamContextualService;
    }

    @Operation(summary = "Recommend a superhero team for a task by prompting technologies.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Superhero team recommended.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuperheroRagTeamDto.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid task description.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access Denied, when false user role",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            )
    })
    @GetMapping("/team/contextual")
    public SuperheroRagTeamDto generateTeam(
            @Parameter(description = "Description of the task for the team")
            @RequestParam String task,

            @Parameter(description = "Requested size of the team")
            @RequestParam(defaultValue = "5")
            int teamSize) {
        return teamContextualService.generateTeamByRag(task, teamSize);
    }
}
