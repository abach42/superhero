package com.abach42.superhero.superhero;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.abach42.superhero.ai.indexing.UpdateSuperheroVectorEvent;
import com.abach42.superhero.shared.api.ApiException;
import com.abach42.superhero.shared.convertion.PatchField;
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
import org.springframework.context.ApplicationEventPublisher;
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

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private SuperheroService subject;

    private Superhero superhero;

    @BeforeEach
    void setUp() {
        subject = new SuperheroService(superheroRepository, 10, passwordEncoder, eventPublisher);
        superhero = TestStubs.getSuperheroStubWithPassword();
    }

    // branch `pageNumber == null`
    @Test
    @DisplayName("Should retrieve superhero list successfully")
    void shouldRetrieveSuperheroList() {
        Page<Superhero> page = new PageImpl<>(
                List.of(superhero), PageRequest.ofSize(1), 1L);
        given(superheroRepository.findAll(
                PageRequest.of(0, 10, Sort.by("id")))).willReturn(page);
        given(superheroRepository.count()).willReturn(1L);

        SuperheroListDto actual = subject.retrieveSuperheroList(null);

        assertNotNull(actual);
    }

    //branch `pageNumber != null && pageNumber > superheroPage.getTotalPages()`
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

    //branch `pageNumber != null && pageNumber <= superheroPage.getTotalPages()`
    @Test
    @DisplayName("Should retrieve superhero list with valid page number")
    void shouldRetrieveSuperheroListWithValidPageNumber() {
        Page<Superhero> page = new PageImpl<>(List.of(superhero),
                PageRequest.ofSize(1), 1L);
        given(superheroRepository.findAll(any(PageRequest.class))).willReturn(page);
        given(superheroRepository.count()).willReturn(1L);

        SuperheroListDto actual = subject.retrieveSuperheroList(0);

        assertNotNull(actual);
    }

    @Test
    @DisplayName("Should throw not found when no superheroes exist")
    void shouldThrowNotFoundWhenNoSuperheroes() {
        Page<Superhero> emptyPage = new PageImpl<>(List.of(),
                PageRequest.ofSize(1), 0L);
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

        SuperheroDto actual = subject.retrieveSuperhero(1L);

        assertThat(actual.alias()).isEqualTo(superhero.getAlias());
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

    @Test
    @DisplayName("Should add superhero successfully - and password is not returned")
    void shouldAddSuperhero() {
        given(passwordEncoder.encode(anyString())).willReturn("encoded");
        given(superheroRepository.save(any(Superhero.class))).willReturn(superhero);
        SuperheroDto dto = SuperheroDto.fromDomain(superhero);

        SuperheroDto actual = subject.addSuperhero(dto);

        assertThat(actual.alias()).isEqualTo(dto.alias());
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
        assertThat(exception.getMessage()).contains(
                SuperheroService.SUPERHERO_NOT_CREATED_MSG_CONSTRAINT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"alias", "realName", "occupation", "originStory"})
    @DisplayName("Should update superhero fields individually")
    void shouldUpdateSuperheroFields(String field) {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));

        SuperheroPatchDto update = createPatchForField(field, "updated");

        SuperheroDto actual = subject.changeSuperhero(1L, update);

        switch (field) {
            case "alias" -> assertThat(actual.alias()).isEqualTo("updated");
            case "realName" -> assertThat(actual.realName()).isEqualTo("updated");
            case "occupation" -> assertThat(actual.occupation()).isEqualTo("updated");
            case "originStory" -> assertThat(actual.originStory()).isEqualTo("updated");
        }
    }

    private SuperheroPatchDto createPatchForField(String field, String value) {
        return switch (field) {
            case "alias" -> new SuperheroPatchDto(PatchField.of(value), null, null,
                    null, null, null);
            case "realName" -> new SuperheroPatchDto(null, PatchField.of(value), null,
                    null, null, null);
            case "occupation" -> new SuperheroPatchDto(null, null, null, null,
                    PatchField.of(value), null);
            case "originStory" -> new SuperheroPatchDto(null, null, null, null,
                    null, PatchField.of(value));
            default -> throw new IllegalArgumentException("Unknown field: " + field);
        };
    }

    @Test
    @DisplayName("Should update date and gender fields")
    void shouldUpdateDateAndGenderFields() {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));

        LocalDate newDate = LocalDate.of(2000, 1, 1);

        SuperheroPatchDto update = new SuperheroPatchDto(
                null,
                null,
                PatchField.of(newDate),
                PatchField.of(Gender.NOT_PROVIDED),
                null,
                null);

        SuperheroDto actual = subject.changeSuperhero(1L, update);

        assertThat(actual.dateOfBirth()).isEqualTo(newDate);
        assertThat(actual.gender()).isEqualTo(Gender.NOT_PROVIDED);
    }

    @Test
    @DisplayName("Should handle empty patch update")
    void shouldHandleEmptyPatchUpdate() {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));

        SuperheroPatchDto emptyUpdate = new SuperheroPatchDto(null, null,
                null, null, null, null);

        SuperheroDto actual = subject.changeSuperhero(1L, emptyUpdate);

        assertThat(actual.alias()).isEqualTo(superhero.getAlias());
        assertThat(actual.realName()).isEqualTo(superhero.getRealName());
    }

    @Test
    @DisplayName("Should set field to null when PatchField.of(null) is provided")
    void shouldSetFieldToNull() {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));

        SuperheroPatchDto update = new SuperheroPatchDto(
                null,                   // missing
                PatchField.of(null),    // explicitly null (delete)
                null, null, null, null
        );

        subject.changeSuperhero(1L, update);

        assertThat(superhero.getRealName()).isNull(); // deleted
        assertThat(superhero.getAlias()).isNotNull(); // missing, origin kept
    }

    @Test
    @DisplayName("Should throw not found when updating non-existent superhero")
    void shouldThrowNotFoundWhenUpdatingNonExistent() {
        given(superheroRepository.findById(1L)).willReturn(Optional.empty());

        SuperheroPatchDto emptyUpdate = new SuperheroPatchDto(null, null,
                null, null, null, null);

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.changeSuperhero(1L, emptyUpdate));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should mark superhero as deleted")
    void shouldMarkSuperheroAsDeleted() {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));
        given(superheroRepository.save(any(Superhero.class))).willReturn(superhero);

        SuperheroDto actual = subject.markSuperheroAsDeleted(1L);

        assertThat(actual).isNotNull();
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

    @Test
    @DisplayName("Should throw bad request on runtime exception during add")
    void shouldThrowBadRequestOnRuntimeExceptionDuringAdd() {
        given(passwordEncoder.encode(anyString())).willReturn("encoded");
        given(superheroRepository.save(any(Superhero.class)))
                .willThrow(new RuntimeException("Generic error"));
        SuperheroDto dto = SuperheroDto.fromDomain(superhero);

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.addSuperhero(dto));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage()).contains(SuperheroService.SUPERHERO_NOT_CREATED_MSG);
    }

    @Test
    @DisplayName("Should throw bad request on exception during change")
    void shouldThrowBadRequestOnExceptionDuringChange() {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));
        // PatchField.updateField throws exception if consumer throws exception
        // We can simulate this by providing a patch that will cause an issue if we had complex
        // setters, but since they are simple, we might need to mock something if possible or just
        // rely on the fact that PatchField.updateField(update.alias(), origin::setAlias) calls
        // the setter.

        // Actually, origin is a real object here (from TestStubs).
        // Using a mock for origin to force an exception.
        Superhero mockSuperhero = org.mockito.Mockito.mock(Superhero.class);
        given(superheroRepository.findById(1L)).willReturn(Optional.of(mockSuperhero));
        org.mockito.Mockito.doThrow(new RuntimeException("Setter failed")).when(mockSuperhero)
                .setAlias(anyString());

        SuperheroPatchDto update = new SuperheroPatchDto(PatchField.of("new"), null,
                null, null, null, null);

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.changeSuperhero(1L, update));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage()).contains("Superhero not updated");
    }

    @Test
    @DisplayName("Should retrieve superheroes in list")
    void shouldRetrieveSuperheroesInList() {
        java.util.Set<Long> ids = java.util.Set.of(1L, 2L);
        given(superheroRepository.findAllById(ids)).willReturn(List.of(superhero));

        List<Superhero> actual = subject.retrieveSuperheroesInList(ids);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(superhero);
    }

    @Test
    @DisplayName("Should retrieve all superheroes")
    void shouldRetrieveAllSuperheroes() {
        given(superheroRepository.findAll()).willReturn(List.of(superhero));

        List<Superhero> actual = subject.retrieveAllSuperheroes();

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(superhero);
    }

    @Test
    @DisplayName("Should not trigger update vector when no change")
    void shouldNotTriggerUpdateVectorWhenNoChange() {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));
        SuperheroPatchDto emptyUpdate = new SuperheroPatchDto(null, null,
                null, null, null, null);

        subject.changeSuperhero(1L, emptyUpdate);

        verify(eventPublisher, org.mockito.Mockito.never()).publishEvent(
                any(UpdateSuperheroVectorEvent.class));
    }

    @Test
    @DisplayName("Should trigger update vector when alias changed")
    void shouldTriggerUpdateVectorWhenAliasChanged() {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));
        SuperheroPatchDto update = new SuperheroPatchDto(PatchField.of("New Alias"),
                null, null, null, null, null);

        subject.changeSuperhero(1L, update);

        verify(eventPublisher).publishEvent(
                any(UpdateSuperheroVectorEvent.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when accessing value of Missing PatchField")
    void shouldThrowIllegalStateExceptionWhenAccessingValueOfMissingPatchField() {
        PatchField<String> missingField = PatchField.missing();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                missingField::value);

        assertThat(exception.getMessage()).isEqualTo("Missing has no value");
    }
}