package com.abach42.superhero.superhero;

import com.abach42.superhero.ai.RemoveSuperheroVectorEvent;
import com.abach42.superhero.ai.UpdateSuperheroVectorEvent;
import com.abach42.superhero.config.api.ApiException;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    public SuperheroService(SuperheroRepository superheroRepository, Integer defaultPageSize,
            PasswordEncoder passwordEncoder, ApplicationEventPublisher eventPublisher) {
        this.superheroRepository = superheroRepository;
        this.defaultPageSize = defaultPageSize;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
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

    public List<Superhero> retrieveSuperheroesInList(Set<Long> heroIds) {
        return superheroRepository.findAllById(heroIds);
    }

    public List<Superhero> retrieveAllSuperheroes() {
        return superheroRepository.findAll();
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

            eventPublisher.publishEvent(new UpdateSuperheroVectorEvent(createdSuperhero));

            return SuperheroDto.fromDomain(createdSuperhero);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, SUPERHERO_NOT_CREATED_MSG_CONSTRAINT);
        } catch (RuntimeException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, SUPERHERO_NOT_CREATED_MSG);
        }
    }

    @Transactional
    public SuperheroDto changeSuperhero(Long id, SuperheroPatchDto update) throws ApiException {
        Superhero origin = getSuperhero(id);
        int origHashCode = origin.hashCode();

        updateField(update.alias(), origin::setAlias);
        updateField(update.realName(), origin::setRealName);
        updateField(update.dateOfBirth(), origin::setDateOfBirth);
        updateField(update.gender(), origin::setGender);
        updateField(update.occupation(), origin::setOccupation);
        updateField(update.originStory(), origin::setOriginStory);

        int changedHashCode = origin.hashCode();
        triggerUpdateVector(origHashCode, changedHashCode, origin);

        return SuperheroDto.fromDomain(origin);
    }

    private <T> void updateField(Optional<T> newValue, Consumer<T> setter) {
        newValue.ifPresent(setter);
    }

    private void triggerUpdateVector(int origHashCode, int changedHashCode, Superhero origin) {
        if (origHashCode == changedHashCode) {
            return;
        }

        eventPublisher.publishEvent(new UpdateSuperheroVectorEvent(origin));
    }

    public SuperheroDto markSuperheroAsDeleted(Long id) throws ApiException {
        Superhero superhero = getSuperhero(id);
        superhero.setDeleted(true);
        superhero.getUser().setDeleted(true);

        superheroRepository.save(superhero);

        eventPublisher.publishEvent(new RemoveSuperheroVectorEvent(superhero));

        return SuperheroDto.fromDomain(superhero);
    }
}