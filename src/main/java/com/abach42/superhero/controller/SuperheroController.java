package com.abach42.superhero.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abach42.superhero.config.PathConfig;
import com.abach42.superhero.entity.dto.SuperheroDto;
import com.abach42.superhero.service.SuperheroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Superhero API")
@RestController
@RequestMapping(PathConfig.BASE_URI + PathConfig.SUPERHEROES)
public class SuperheroController {
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
            description = "Superheroes not found",
            content = @Content 
        )})
    @GetMapping
    public ResponseEntity<?> getAllSuperheros() {
        List<SuperheroDto> superheroes = superheroService.getAllSuperheros();
    
        if (superheroes.isEmpty()) {
            return ResponseEntity.notFound().build();
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
            description = "Superhero not found",
            content = @Content 
        )})
    @GetMapping("{id}")
    public ResponseEntity<SuperheroDto> getSuperhero(@PathVariable Long id) {
        return ResponseEntity.of(superheroService.getSuperhero(id));
    }
}