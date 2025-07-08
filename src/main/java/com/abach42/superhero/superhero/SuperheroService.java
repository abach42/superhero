package com.abach42.superhero.superhero;

import com.abach42.superhero.config.api.ApiException;
import jakarta.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuperheroService {

    public static final String SUPERHEROES_NOT_FOUND_MSG = "Superheroes not found.";
    public static final String MAX_PAGE_EXCEEDED_MSG = "The total page number has been exceeded.";
    public static final String SUPERHERO_NOT_FOUND_MSG = "Superhero not found on id ";
    public static final String SUPERHERO_NOT_CREATED_MSG = "Superhero could not be written.";
    public static final String SUPERHERO_NOT_CREATED_MSG_CONSTRAINT = "Superhero could not be written, because email already exists.";

    private final SuperheroRepository superheroRepository;
    private final Integer defaultPageSize;
    private final PasswordEncoder passwordEncoder;

    public SuperheroService(SuperheroRepository superheroRepository, Integer defaultPageSize,
            PasswordEncoder passwordEncoder) {
        this.superheroRepository = superheroRepository;
        this.defaultPageSize = defaultPageSize;
        this.passwordEncoder = passwordEncoder;
    }

    public SuperheroListDto retrieveSuperheroList(@Nullable Integer pageNumber)
            throws ApiException {
        Page<SuperheroDto> superheroPage = superheroRepository
                .findAll(PageRequest.of(Optional.ofNullable(pageNumber).orElse(0), defaultPageSize,
                        Sort.by("id")))
                .map(SuperheroDto::fromDomain);

        if (pageNumber != null && pageNumber > superheroPage.getTotalPages()) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY,
                    String.format("%s Total: %s, requested: %s.", MAX_PAGE_EXCEEDED_MSG,
                            superheroPage.getTotalPages(),
                            pageNumber));
        }

        if (superheroPage.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, SUPERHEROES_NOT_FOUND_MSG);
        }

        return SuperheroListDto.fromPage(superheroPage, superheroRepository.count());
    }

    public SuperheroDto retrieveSuperhero(Long id) throws ApiException {
        Superhero superhero = getSuperhero(id);
        return SuperheroDto.fromDomain(superhero);
    }

    public Superhero getSuperhero(Long id) throws ApiException {
        return superheroRepository.findById(id).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, SUPERHERO_NOT_FOUND_MSG + id));
    }

    public SuperheroDto addSuperhero(SuperheroDto superheroDto) throws ApiException {
        try {
            Objects.requireNonNull(superheroDto);

            Superhero newSuperhero = SuperheroDto.toDomain(superheroDto);
            String encodedPassword = passwordEncoder.encode(newSuperhero.getUser().getPassword());
            newSuperhero.getUser().setPassword(encodedPassword);

            Superhero createdSuperhero = superheroRepository.save(newSuperhero);

            return SuperheroDto.fromDomain(createdSuperhero);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, SUPERHERO_NOT_CREATED_MSG_CONSTRAINT);
        } catch (RuntimeException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, SUPERHERO_NOT_CREATED_MSG);
        }
    }

    @Transactional
    public SuperheroDto changeSuperhero(Long id, SuperheroDto update) throws ApiException {
        Superhero origin = getSuperhero(id);

        // Use a more sophisticated approach to handle explicit nulls
        updateFieldIfPresent(update.alias(), origin::setAlias);
        updateFieldIfPresent(update.realName(), origin::setRealName);
        updateFieldIfPresent(update.dateOfBirth(), origin::setDateOfBirth);
        updateFieldIfPresent(update.gender(), origin::setGender);
        updateFieldIfPresent(update.occupation(), origin::setOccupation);
        updateFieldIfPresent(update.originStory(), origin::setOriginStory);

        return SuperheroDto.fromDomain(origin);
    }

    private <T> void updateFieldIfPresent(T value, java.util.function.Consumer<T> setter) {
        // This still has the same limitation - we can't distinguish between
        // "not provided" and "explicitly null"
        if (value != null) {
            setter.accept(value);
        }
    }

    public SuperheroDto markSuperheroAsDeleted(Long id) throws ApiException {
        Superhero superhero = getSuperhero(id);
        superhero.setDeleted(true);
        superhero.getUser().setDeleted(true);

        superheroRepository.save(superhero);

        return SuperheroDto.fromDomain(superhero);
    }
}