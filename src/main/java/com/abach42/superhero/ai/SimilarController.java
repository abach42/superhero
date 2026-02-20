package com.abach42.superhero.ai;

import com.abach42.superhero.login.methodsecurity.IsAdmin;
import com.abach42.superhero.shared.api.ErrorDto;
import com.abach42.superhero.shared.api.PathConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Find groups of similar heroes.")
@RestController
@RequestMapping(path = PathConfig.SUPERHEROES)
@SecurityRequirement(name = "Bearer Authentication")
@IsAdmin
public class SimilarController {

    private final SimilarService similarService;

    public SimilarController(
            SimilarService similarService) {
        this.similarService = similarService;
    }

    @Operation(summary = "Search for similar superheroes")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Similar superheroes found.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = SemanticMatch.class)
                                    )
                            )
                    }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search query.",
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
    @GetMapping("/search")
    public List<RelevantSuperheroesDto> searchSimilar(
            @Parameter(description = "Description of the superhero to search for") @RequestParam String query,
            @Parameter(description = "Maximum number of results to return") @RequestParam(defaultValue = "5") int quantity) {
        return similarService.searchSimilarHeroes(query, quantity);
    }
}
