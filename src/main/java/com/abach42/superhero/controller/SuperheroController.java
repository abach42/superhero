package com.abach42.superhero.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.repository.SuperheroRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/superheros/")
public class SuperheroController {
    private final SuperheroRepository superheroRepository;

    public SuperheroController(SuperheroRepository superheroRepository) {
        this.superheroRepository = superheroRepository;
    }

    @Operation(summary = "Get all superheros")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "Superheros found",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        implementation = Superhero.class)
                ) 
            }), 
        @ApiResponse( 
            responseCode = "404", 
            description = "Superheros not found",
            content = @Content 
        )})
    @GetMapping
    public List<Superhero> getAllSuperheros() {
        return superheroRepository.findAll();
    }
}