package com.abach42.superhero.controller;

import java.net.URI;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abach42.superhero.config.PathConfig;
import com.abach42.superhero.entity.dto.ErrorResponse;
import com.abach42.superhero.entity.dto.SuperheroDto;
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
    public static final String SUPERHEROES_NOT_FOUND_MSG = "Superheroes not found";
    public static final String SUPERHERO_NOT_FOUND_MSG = "Superhero not found on id ";
    public static final String SUPERHERO_NOT_CREATED_MSG = "Superhero could not be written";

    private final SuperheroService superheroService;

    public SuperheroController(SuperheroService superheroService) {
        this.superheroService = superheroService;
    }

    //localize birth date
    //todo paginate
    //todo deleted I/0

    @Operation(summary = "Get all superheroes")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "Superheroes found",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(       
                        schema = @Schema(
                            implementation = SuperheroDto.class)
                    )
                )
            }), 
        @ApiResponse( 
            responseCode = "404",
            description = SUPERHEROES_NOT_FOUND_MSG,
            content = @Content 
        )
    })
    @GetMapping
    public ResponseEntity<?> getAllSuperheros() {
        List<SuperheroDto> superheroes = superheroService.getAllSuperheros();
    
        if (superheroes.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, SUPERHEROES_NOT_FOUND_MSG);
        }
            
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
            description = SUPERHERO_NOT_FOUND_MSG,
            content = @Content 
        )
    })
    @GetMapping("{id}")
    public ResponseEntity<SuperheroDto> getSuperhero(@PathVariable Long id) {
        return ResponseEntity.ok(superheroService.getSuperhero(id).orElseThrow(
            () -> new ApiException(HttpStatus.NOT_FOUND, SUPERHERO_NOT_FOUND_MSG + id)
        ));
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
            description = SUPERHERO_NOT_CREATED_MSG,
            content = @Content 
        )
    })
    @PostMapping 
    public ResponseEntity<SuperheroDto> createSuperhero(@Valid @RequestBody SuperheroDto superheroDto) {
        try {
            SuperheroDto createdSuperheroDto = superheroService.addSuperhero(superheroDto);
            return ResponseEntity.created(
                        URI.create(PathConfig.SUPERHEROES + "/" + createdSuperheroDto.id())
                    )
                    .body(createdSuperheroDto);
        } catch (ApiException | NullPointerException | IllegalArgumentException | DataAccessException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, SUPERHERO_NOT_CREATED_MSG);
        }
    }
}