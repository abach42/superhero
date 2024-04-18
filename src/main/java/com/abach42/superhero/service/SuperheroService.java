package com.abach42.superhero.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abach42.superhero.controller.SuperheroController;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.dto.SuperheroDto;
import com.abach42.superhero.entity.dto.SuperheroListDto;
import com.abach42.superhero.exception.ApiException;
import com.abach42.superhero.repository.SuperheroRepository;

import jakarta.annotation.Nullable;

@Service
public class SuperheroService {
    public static final String MAX_PAGE_EXEEDED_MSG = "The total page number has been exceeded.";
    public static final String SUPERHEROES_NOT_FOUND_MSG = "Superheroes not found";

    private final SuperheroRepository superheroRepository;
    private final Integer defaultPageSize;

    public SuperheroService(SuperheroRepository superheroRepository, Integer defaultPageSize) {
        this.superheroRepository = superheroRepository;
        this.defaultPageSize = defaultPageSize;
    }
    
    public SuperheroListDto getAllSuperheros(@Nullable Integer pageNumber) {
        Page<SuperheroDto> superheroPage = superheroRepository
            .findAll(PageRequest.of(Optional.ofNullable(pageNumber).orElse(0), defaultPageSize))
            .map(SuperheroDto::fromDomain);

        if(pageNumber != null && pageNumber > superheroPage.getTotalPages()) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, 
            String.format("%s Total: %s, requested: %s.", MAX_PAGE_EXEEDED_MSG, superheroPage.getTotalPages(), pageNumber));
        }

        if (superheroPage.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, SUPERHEROES_NOT_FOUND_MSG);
        }

        return SuperheroListDto.fromPage(superheroPage);
    }

    public Optional<SuperheroDto> getSuperhero(Long id) {
        return superheroRepository.findById(id).map(SuperheroDto::fromDomain);
    }

    public SuperheroDto addSuperhero(SuperheroDto superheroDto)  {
        Objects.requireNonNull(superheroDto);

        Superhero newSuperhero = SuperheroDto.toDomain(superheroDto);
        Superhero createdSuperhero = superheroRepository.save(newSuperhero);

        return SuperheroDto.fromDomain(createdSuperhero);
    }

    /* 
     * Merge and write manually superheroDTO, using `JPA automatic dirty checking` by not 
     * merging a null value. 
     * todo: @DynamicUpdate over entity
     * todo: get this merge done by framework solution, but including *Dto
     */
    @Transactional
    public SuperheroDto updateSuperhero(Long id, SuperheroDto update) {
        Optional<Superhero> maybeSuperhero = superheroRepository.findById(id);

        Superhero updatedSuperhero = maybeSuperhero.map(
            origin -> {
                if(update.alias() != null) origin.setAlias(update.alias());
                if(update.realName() != null) origin.setRealName(update.realName());
                if(update.dateOfBirth() != null) origin.setDateOfBirth(update.dateOfBirth());
                if(update.gender() != null) origin.setGender(update.gender());
                if(update.occupation() != null) origin.setOccupation(update.occupation());
                return origin;
            }  
        ).orElseThrow(
            () -> new ApiException(HttpStatus.NOT_FOUND, SuperheroController.SUPERHERO_NOT_FOUND_MSG + id));

        Superhero savedSuperhero = superheroRepository.save(updatedSuperhero);
        return SuperheroDto.fromDomain(savedSuperhero);
    }
    
}