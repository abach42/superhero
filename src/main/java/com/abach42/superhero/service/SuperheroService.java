package com.abach42.superhero.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.dto.SuperheroDto;
import com.abach42.superhero.repository.SuperheroRepository;

@Service
public class SuperheroService {

    private final SuperheroRepository superheroRepository;

    public SuperheroService(SuperheroRepository superheroRepository) {
        this.superheroRepository = superheroRepository;
    }
    
    public List<SuperheroDto> getAllSuperheros() {
        return superheroRepository.findAll()
            .stream().map(SuperheroDto::fromDomain)
            .collect(Collectors.toList());
    }

    public Optional<SuperheroDto> getSuperhero(Long id) {
        return superheroRepository.findById(id).map(SuperheroDto::fromDomain);
    }

    public SuperheroDto addSuperhero(SuperheroDto superheroDto)  {
        Objects.requireNonNull(superheroDto);

        Superhero newSuperhero = new Superhero(
            superheroDto.alias(),
            superheroDto.realName(),
            superheroDto.dateOfBirth(),
            superheroDto.gender(),
            superheroDto.occupation(),
            null //todo
        );

        Superhero createdSuperhero = superheroRepository.save(newSuperhero);
        return SuperheroDto.fromDomain(createdSuperhero);
    }
}