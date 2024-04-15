package com.abach42.superhero.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.repository.SuperheroRepository;

@RestController
@RequestMapping("/api/superheros/")
public class SuperheroController {
    private final SuperheroRepository superheroRepository;

    public SuperheroController(SuperheroRepository superheroRepository) {
        this.superheroRepository = superheroRepository;
    }

    @GetMapping
    public List<Superhero> getAllSuperheros() {
        return superheroRepository.findAll();
    }
}