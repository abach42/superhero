package com.abach42.superhero.ai;

import com.abach42.superhero.shared.api.PathConfig;
import com.abach42.superhero.login.methodsecurity.IsAdmin;
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
//todo test
//todo api doc
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/team")
    public SuperheroTeam recommendTeam(@RequestParam String task,
            @RequestParam(defaultValue = "3") int teamSize) {
        return teamService.recommendTeam(task, teamSize);
    }
}
