package com.abach42.superhero.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abach42.superhero.config.OnCreate;
import com.abach42.superhero.config.OnUpdate;
import com.abach42.superhero.config.PathConfig;
import com.abach42.superhero.entity.dto.ErrorResponse;
import com.abach42.superhero.entity.dto.SuperheroDto;
import com.abach42.superhero.entity.dto.SuperheroListDto;
import com.abach42.superhero.exception.ApiException;
import com.abach42.superhero.service.SuperheroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Superhero API")
@RestController
@RequestMapping(path = PathConfig.SUPERHEROES)
public class SuperheroController {
    private final SuperheroService superheroService;

    public SuperheroController(SuperheroService superheroService) {
        this.superheroService = superheroService;
    }

    // localize birth date
    // todo deleted I/0

    /*
     * List of all superheroes with simple paging
     * and without page metadata return, to keep response simple
     * on a simple entity.
     */
    @Operation(summary = "Get all superheroes")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "Superheroes found",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(       
                        schema = @Schema(
                            implementation = SuperheroListDto.class)
                    )
                )
            }), 
            @ApiResponse(responseCode = "404", description = SuperheroService.SUPERHEROES_NOT_FOUND_MSG, content = @Content),
            @ApiResponse(responseCode = "422", description = SuperheroService.MAX_PAGE_EXEEDED_MSG, content = @Content)
    })
    @GetMapping
    public ResponseEntity<?> getAllSuperheroesPaginated(@RequestParam(required = false) Integer page) {
        SuperheroListDto superheroes = superheroService.getAllSuperheros(page);
        return ResponseEntity.ok(superheroes);
    }

    @Operation(summary = "Get superhero")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "Superhero found",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        implementation = SuperheroDto.class)
                ) 
            }), 
        @ApiResponse( 
            responseCode = "404", 
            description = SuperheroService.SUPERHERO_NOT_FOUND_MSG,
            content = @Content 
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<SuperheroDto> getSuperhero(@PathVariable Long id) throws ApiException {
        return ResponseEntity.ok(superheroService.getSuperhero(id));
    }

    @Operation(summary = "Add new superhero")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201", description = "Superhero created",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        implementation = SuperheroDto.class)
                ) 
            }),
        @ApiResponse( 
            responseCode = "422", 
            description = "Validation error. Check 'errors' field for details.",
            content =  @Content(
                mediaType = "application/json",
                schema = @Schema(
                    implementation = ErrorResponse.class)
            )  
        ), 
        @ApiResponse( 
            responseCode = "400", 
            description = SuperheroService.SUPERHERO_NOT_CREATED_MSG,
            content = @Content 
        )
    })
    @Validated(OnCreate.class)
    @PostMapping
    public ResponseEntity<SuperheroDto> createSuperhero(@Valid @RequestBody SuperheroDto superheroDto)
            throws ApiException, MethodArgumentNotValidException {
        try {
            SuperheroDto createdSuperheroDto = superheroService.addSuperhero(superheroDto);
            return ResponseEntity.created(
                    URI.create(PathConfig.SUPERHEROES + "/" + createdSuperheroDto.id()))
                    .body(createdSuperheroDto);
        } catch (RuntimeException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, SuperheroService.SUPERHERO_NOT_CREATED_MSG + "cause " + e.getCause());
        }
        // TODO WRITE TEST
        // https://reflectoring.io/spring-boot-exception-handling/#controlleradvice
    }

    @Validated(OnUpdate.class)
    @PutMapping("/{id}")
    public ResponseEntity<SuperheroDto> updateSuperhero(@PathVariable(required = true) Long id,
            @Valid @RequestBody SuperheroDto superheroDto) throws ApiException {
        SuperheroDto updatedSuperheroDto = superheroService.updateSuperhero(id, superheroDto);
        return ResponseEntity.ok(updatedSuperheroDto);
        // TODO WRITE TEST
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuperheroDto> markSuperheroDeleted(@PathVariable Long id) {

        // TODO Provide a cli command to delete marked as deleted records/ consider to
        // run a scheduler
    }
}