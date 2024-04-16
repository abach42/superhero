package com.abach42.superhero.controller;

import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abach42.superhero.entity.dto.SuperheroDto;
import com.abach42.superhero.repository.SuperheroRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/superheros/")
public class SuperheroController {
    //todo make service for request
    //todo map to DTO
    //localize birth date
    //todo paginate
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
                        implementation = SuperheroDto.class)
                ) 
            }), 
        @ApiResponse( 
            responseCode = "404", 
            description = "Superheros not found",
            content = @Content 
        )})
    @GetMapping
    public Stream<SuperheroDto> getAllSuperheros() {
        return superheroRepository.findAll().stream().map(SuperheroDto::fromDomain);
    }
}