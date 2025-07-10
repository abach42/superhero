package com.abach42.superhero.superhero;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.abach42.superhero.config.api.ApiException;
import com.abach42.superhero.testconfiguration.TestStubs;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class SuperheroServiceTest {

    @Mock
    private SuperheroRepository superheroRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private SuperheroService subject;

    private Superhero superhero;

    @BeforeEach
    void setUp() {
        subject = new SuperheroService(superheroRepository, 10, passwordEncoder);
        superhero = TestStubs.getSuperheroStubWithPassword();
    }

    @Test
    @DisplayName("Should retrieve superhero list successfully")
    void shouldRetrieveSuperheroList() {
        Page<Superhero> page = new PageImpl<>(List.of(superhero), PageRequest.ofSize(1), 1L);
        given(superheroRepository.findAll(PageRequest.of(0, 10, Sort.by("id")))).willReturn(page);
        given(superheroRepository.count()).willReturn(1L);

        SuperheroListDto result = subject.retrieveSuperheroList(null);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw unprocessable entity when page number exceeds total pages")
    void shouldThrowUnprocessableEntityWhenPageExceedsTotal() {
        Page<Superhero> page = new PageImpl<>(List.of(), PageRequest.ofSize(1), 0L);
        given(superheroRepository.findAll(any(PageRequest.class))).willReturn(page);

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.retrieveSuperheroList(2));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(exception.getMessage()).contains(SuperheroService.MAX_PAGE_EXCEEDED_MSG);
    }

    @Test
    @DisplayName("Should throw not found when no superheroes exist")
    void shouldThrowNotFoundWhenNoSuperheroes() {
        Page<Superhero> emptyPage = new PageImpl<>(List.of(), PageRequest.ofSize(1), 0L);
        given(superheroRepository.findAll(any(PageRequest.class))).willReturn(emptyPage);

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.retrieveSuperheroList(null));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).contains(SuperheroService.SUPERHEROES_NOT_FOUND_MSG);
    }

    @Test
    @DisplayName("Should retrieve superhero by id")
    void shouldRetrieveSuperheroById() {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));

        SuperheroDto result = subject.retrieveSuperhero(1L);

        assertThat(result.alias()).isEqualTo(superhero.getAlias());
    }

    @Test
    @DisplayName("Should throw not found when superhero doesn't exist")
    void shouldThrowNotFoundWhenSuperheroNotExists() {
        given(superheroRepository.findById(1L)).willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.retrieveSuperhero(1L));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).contains(SuperheroService.SUPERHERO_NOT_FOUND_MSG);
    }

    // Test addSuperhero method
    @Test
    @DisplayName("Should add superhero successfully")
    void shouldAddSuperhero() {
        given(passwordEncoder.encode(anyString())).willReturn("encoded");
        given(superheroRepository.save(any(Superhero.class))).willReturn(superhero);
        SuperheroDto dto = SuperheroDto.fromDomain(superhero);

        SuperheroDto result = subject.addSuperhero(dto);

        assertThat(result.alias()).isEqualTo(dto.alias());
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    @DisplayName("Should throw bad request when adding null superhero")
    void shouldThrowBadRequestWhenAddingNull() {
        ApiException exception = assertThrows(ApiException.class,
                () -> subject.addSuperhero(null));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage()).contains(SuperheroService.SUPERHERO_NOT_CREATED_MSG);
    }

    @Test
    @DisplayName("Should throw bad request on data integrity violation")
    void shouldThrowBadRequestOnDataIntegrityViolation() {
        given(passwordEncoder.encode(anyString())).willReturn("encoded");
        given(superheroRepository.save(any(Superhero.class)))
                .willThrow(new DataIntegrityViolationException("Email exists"));
        SuperheroDto dto = SuperheroDto.fromDomain(superhero);

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.addSuperhero(dto));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage()).contains(SuperheroService.SUPERHERO_NOT_CREATED_MSG_CONSTRAINT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"alias", "realName", "occupation", "originStory"})
    @DisplayName("Should update superhero fields individually")
    void shouldUpdateSuperheroFields(String field) {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));

        SuperheroPatchDto update = createPatchForField(field, "updated");

        SuperheroDto result = subject.changeSuperhero(1L, update);

        switch (field) {
            case "alias" -> assertThat(result.alias()).isEqualTo("updated");
            case "realName" -> assertThat(result.realName()).isEqualTo("updated");
            case "occupation" -> assertThat(result.occupation()).isEqualTo("updated");
            case "originStory" -> assertThat(result.originStory()).isEqualTo("updated");
        }
    }

    @Test
    @DisplayName("Should update date and gender fields")
    void shouldUpdateDateAndGenderFields() {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));

        LocalDate newDate = LocalDate.of(2000, 1, 1);
        SuperheroPatchDto update = new SuperheroPatchDto(
                Optional.empty(), Optional.empty(), Optional.of(newDate),
                Optional.of(Gender.HIDDEN), Optional.empty(), Optional.empty());

        SuperheroDto result = subject.changeSuperhero(1L, update);

        assertThat(result.dateOfBirth()).isEqualTo(newDate);
        assertThat(result.gender()).isEqualTo(Gender.HIDDEN);
    }

    @Test
    @DisplayName("Should handle empty patch update")
    void shouldHandleEmptyPatchUpdate() {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));

        SuperheroDto result = subject.changeSuperhero(1L, SuperheroPatchDto.create());

        assertThat(result.alias()).isEqualTo(superhero.getAlias());
    }

    @Test
    @DisplayName("Should throw not found when updating non-existent superhero")
    void shouldThrowNotFoundWhenUpdatingNonExistent() {
        given(superheroRepository.findById(1L)).willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.changeSuperhero(1L, SuperheroPatchDto.create()));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should mark superhero as deleted")
    void shouldMarkSuperheroAsDeleted() {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));
        given(superheroRepository.save(any(Superhero.class))).willReturn(superhero);

        SuperheroDto result = subject.markSuperheroAsDeleted(1L);

        assertThat(result).isNotNull();
        verify(superheroRepository).save(any(Superhero.class));
    }

    @Test
    @DisplayName("Should throw not found when deleting non-existent superhero")
    void shouldThrowNotFoundWhenDeletingNonExistent() {
        given(superheroRepository.findById(1L)).willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.markSuperheroAsDeleted(1L));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private SuperheroPatchDto createPatchForField(String field, String value) {
        return switch (field) {
            case "alias" -> new SuperheroPatchDto(Optional.of(value), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
            case "realName" -> new SuperheroPatchDto(Optional.empty(), Optional.of(value),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
            case "occupation" -> new SuperheroPatchDto(Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.of(value), Optional.empty());
            case "originStory" -> new SuperheroPatchDto(Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(value));
            default -> SuperheroPatchDto.create();
        };
    }
}