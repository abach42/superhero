package com.abach42.superhero.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.dto.SuperheroDto;
import com.abach42.superhero.entity.dto.SuperheroListDto;
import com.abach42.superhero.exception.ApiException;
import com.abach42.superhero.repository.SuperheroRepository;

import jakarta.annotation.Nullable;

@Service
public class SuperheroService {
    public static final String SUPERHEROES_NOT_FOUND_MSG = "Superheroes not found.";
    public static final String MAX_PAGE_EXEEDED_MSG = "The total page number has been exceeded.";
    public static final String SUPERHERO_NOT_FOUND_MSG = "Superhero not found on id ";
    public static final String SUPERHERO_NOT_CREATED_MSG = "Superhero could not be written.";

    private final SuperheroRepository superheroRepository;
    private final Integer defaultPageSize;

    public SuperheroService(SuperheroRepository superheroRepository, Integer defaultPageSize) {
        this.superheroRepository = superheroRepository;
        this.defaultPageSize = defaultPageSize;
    }

    public SuperheroListDto getAllSuperheros(@Nullable Integer pageNumber) throws ApiException {
        Page<SuperheroDto> superheroPage = superheroRepository
                .findAll(PageRequest.of(Optional.ofNullable(pageNumber).orElse(0), defaultPageSize, Sort.by("id")))
                .map(SuperheroDto::fromDomain);

        if (pageNumber != null && pageNumber > superheroPage.getTotalPages()) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY,
                    String.format("%s Total: %s, requested: %s.", MAX_PAGE_EXEEDED_MSG, superheroPage.getTotalPages(),
                            pageNumber));
        }

        if (superheroPage.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, SUPERHEROES_NOT_FOUND_MSG);
        }

        return SuperheroListDto.fromPage(superheroPage, superheroRepository.count());
    }

    public SuperheroDto getSupherheroConverted(Long id) throws ApiException {
        Superhero superhero = getSuperhero(id);
        return SuperheroDto.fromDomain(superhero);
    }

    private Superhero getSuperhero(Long id) throws ApiException {
        return superheroRepository.findById(id).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, SUPERHERO_NOT_FOUND_MSG + id));
    }

    public SuperheroDto addSuperhero(SuperheroDto superheroDto) throws ApiException {
       try {
            Objects.requireNonNull(superheroDto);

            Superhero newSuperhero = SuperheroDto.toDomain(superheroDto);
            Superhero createdSuperhero = superheroRepository.save(newSuperhero);

            return SuperheroDto.fromDomain(createdSuperhero);
        } catch (RuntimeException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, SUPERHERO_NOT_CREATED_MSG);
        }
    }

    /*
     * TODO: Merge and write manually superheroDTO, using `JPA automatic dirty checking`
     * by not merging a null value.
     * * @DynamicUpdate over entity
     * * get this merge done by framework solution, but including *Dto
     * * @Transactional over here to enable dirty checking
     */
    public SuperheroDto updateSuperhero(Long id, SuperheroDto update) throws ApiException {
        Superhero origin = getSuperhero(id);

        if (update.alias() != null) {
            origin.setAlias(update.alias());
        }
        if (update.realName() != null) {
            origin.setRealName(update.realName());
        }
        if (update.dateOfBirth() != null) {
            origin.setDateOfBirth(update.dateOfBirth());
        }
        if (update.gender() != null) {
            origin.setGender(update.gender());
        }
        if (update.occupation() != null) {
            origin.setOccupation(update.occupation());
        }

        Superhero savedSuperhero = superheroRepository.save(origin);
        return SuperheroDto.fromDomain(savedSuperhero);
    }

    public SuperheroDto markSuperheroAsDeleted(Long id) throws ApiException {
        Superhero superhero = getSuperhero(id);
        superhero.setDeleted(true);

        superheroRepository.save(superhero);

        return SuperheroDto.fromDomain(superhero);
    }
}