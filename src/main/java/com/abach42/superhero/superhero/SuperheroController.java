package com.abach42.superhero.superhero;

import com.abach42.superhero.config.api.ApiException;
import com.abach42.superhero.config.api.ErrorDetailedDto;
import com.abach42.superhero.config.api.ErrorDto;
import com.abach42.superhero.config.api.PathConfig;
import com.abach42.superhero.config.validation.OnCreate;
import com.abach42.superhero.config.validation.OnUpdate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Superhero API")
@RestController
@RequestMapping(path = PathConfig.SUPERHEROES)
@SecurityRequirement(name = "Bearer Authentication")
@IsAdmin
public class SuperheroController {

    private final SuperheroService superheroService;

    public SuperheroController(SuperheroService superheroService) {
        this.superheroService = superheroService;
    }

    /*
     * List of all superheroes with simple paging
     * and without page metadata return, to keep response simple
     * on a simple entity.
     */
    @Operation(summary = "Get all superheroes")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Superheroes found.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = SuperheroListDto.class)
                                    )
                            )
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = SuperheroService.SUPERHEROES_NOT_FOUND_MSG,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = SuperheroService.MAX_PAGE_EXCEEDED_MSG,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            )
    })
    @IsUser
    @GetMapping
    public ResponseEntity<SuperheroListDto> listSuperheroesPaginated(
            @RequestParam(required = false) Integer page)
            throws ApiException {
        SuperheroListDto superheroes = superheroService.retrieveSuperheroList(page);
        return ResponseEntity.ok(superheroes);
    }

    @Operation(summary = "Get superhero")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Superhero found.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuperheroDto.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = SuperheroService.SUPERHERO_NOT_FOUND_MSG,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            )
    })

    @IsUser
    @GetMapping("/{id}")
    public ResponseEntity<SuperheroDto> showSuperhero(@PathVariable Long id) throws ApiException {
        return ResponseEntity.ok(superheroService.retrieveSuperhero(id));
    }

    @Operation(summary = "Add new superhero")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Superhero created.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuperheroDto.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid request content.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailedDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = SuperheroService.SUPERHERO_NOT_CREATED_MSG,
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
    @PostMapping
    public ResponseEntity<SuperheroDto> createSuperhero(
            @Validated(OnCreate.class) @RequestBody SuperheroDto superheroDto)
            throws ApiException {
        SuperheroDto createdSuperheroDto = superheroService.addSuperhero(superheroDto);
        URI uri = UriComponentsBuilder.fromUriString(PathConfig.SUPERHEROES + "/{id}")
                .build(createdSuperheroDto.id());
        return ResponseEntity.created(uri).body(createdSuperheroDto);
    }

    @Operation(summary = "Update existing superhero")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Superhero updated",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuperheroDto.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = SuperheroService.SUPERHERO_NOT_FOUND_MSG,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request content",
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
    @PatchMapping("/{id}")
    public ResponseEntity<SuperheroDto> updateSuperhero(@PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody SuperheroPatchDto superheroDto) throws ApiException {
        SuperheroDto updatedSuperheroDto = superheroService.changeSuperhero(id, superheroDto);
        return ResponseEntity.ok(updatedSuperheroDto);
    }

    @Operation(summary = "Delete superhero (mark as deleted)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Superhero deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuperheroDto.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = SuperheroService.SUPERHERO_NOT_FOUND_MSG,
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
    @DeleteMapping("/{id}")
    public ResponseEntity<SuperheroDto> softDeleteSuperhero(@PathVariable Long id) {
        SuperheroDto updatedSuperheroDto = superheroService.markSuperheroAsDeleted(id);
        return ResponseEntity.ok(updatedSuperheroDto);
    }
}