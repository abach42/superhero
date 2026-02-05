package com.abach42.superhero.ai;

import com.abach42.superhero.login.methodsecurity.IsAdmin;
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

@Tag(name = "Superhero API")
@RestController
@RequestMapping(path = PathConfig.SUPERHEROES)
@SecurityRequirement(name = "Bearer Authentication")
@IsAdmin
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @Operation(summary = "Recommend a superhero team for a task")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Superhero team recommended.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuperheroTeam.class)
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
    @GetMapping("/team")
    public SuperheroTeam recommendTeam(
            @Parameter(description = "Description of the task for the team") @RequestParam String task,
            @Parameter(description = "Requested size of the team") @RequestParam(defaultValue = "3") int teamSize) {
        return teamService.recommendTeam(task, teamSize);
    }
}
