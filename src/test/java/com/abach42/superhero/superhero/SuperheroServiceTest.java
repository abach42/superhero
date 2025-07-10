package com.abach42.superhero.superhero;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.abach42.superhero.config.api.ApiException;
import com.abach42.superhero.testconfiguration.TestStubs;
import com.abach42.superhero.user.ApplicationUser;
import com.abach42.superhero.user.UserRole;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class SuperheroServiceTest {

    @Mock
    private SuperheroRepository superheroRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SuperheroService subject;

    private Superhero superhero;

    private static Stream<Arguments> updateFieldByField() {
        ApplicationUser user = new ApplicationUser("unique", "foo", UserRole.USER);

        return Stream.of(
                Arguments.of(new SuperheroPatchDto(Optional.of("updated"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()),
                        new Superhero("updated", "bar", LocalDate.of(1970, 1, 1), Gender.MALE, "foo",
                                "foo", user)),
                Arguments.of(new SuperheroPatchDto(Optional.empty(), Optional.of("updated"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()),
                        new Superhero("some", "updated", LocalDate.of(1970, 1, 1), Gender.MALE, "foo",
                                "foo", user)),
                Arguments.of(new SuperheroPatchDto(Optional.empty(), Optional.empty(), Optional.of(LocalDate.of(1999, 1, 1)), Optional.empty(), Optional.empty(), Optional.empty()),
                        new Superhero("some", "bar", LocalDate.of(1999, 1, 1), Gender.MALE, "foo", "foo",
                                user)),
                Arguments.of(new SuperheroPatchDto(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Gender.HIDDEN), Optional.empty(), Optional.empty()),
                        new Superhero("some", "bar", LocalDate.of(1970, 1, 1), Gender.HIDDEN, "foo",
                                "foo", user)),
                Arguments.of(new SuperheroPatchDto(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("updated"), Optional.empty()),
                        new Superhero("some", "bar", LocalDate.of(1970, 1, 1), Gender.MALE, "updated",
                                "foo", user)),
                Arguments.of(new SuperheroPatchDto(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("updated")),
                        new Superhero("some", "bar", LocalDate.of(1970, 1, 1), Gender.MALE, "foo",
                                "updated", user)),
                Arguments.of(SuperheroPatchDto.create(),
                        new Superhero("some", "bar", LocalDate.of(1970, 1, 1), Gender.MALE, "foo", "foo",
                                user))
        );
    }


    @BeforeEach
    public void setUp() {
        // Manually create the service with constructor injection
        subject = new SuperheroService(superheroRepository, 10, passwordEncoder);
        superhero = TestStubs.getSuperheroStubWithPassword();
    }

    @Test
    @DisplayName("Get all heroes, get first page of heroes by null page")
    public void testRetrieveSuperheroList() {
        Page<Superhero> page = new PageImpl<>(List.of(superhero), PageRequest.ofSize(1), 1L);

        given(superheroRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")))))
                .willReturn(page);
        given(superheroRepository.count()).willReturn(1L);

        SuperheroListDto actual = subject.retrieveSuperheroList(null);

        assertNotNull(actual);
    }

    @Test
    @DisplayName("Get all heroes, throws unprocessable, because of pageNumber is higher than total pages")
    void testRetrieveSuperheroListThrowsUnprocessableEntity() {
        Page<Superhero> page = new PageImpl<>(List.of(), PageRequest.ofSize(1), 0L);

        given(superheroRepository.findAll(any(PageRequest.class))).willReturn(page);

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.retrieveSuperheroList(2));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(exception.getMessage()).containsSequence(SuperheroService.MAX_PAGE_EXCEEDED_MSG);
    }

    @Test
    @DisplayName("Get all heroes throws not found, because superheroPage is empty")
    void testRetrieveSuperheroListThrowsNotFound() {
        Page<Superhero> page = new PageImpl<>(List.of(), PageRequest.ofSize(1), 0L);

        given(superheroRepository.findAll(any(PageRequest.class))).willReturn(page);

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.retrieveSuperheroList(null));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).containsSequence(
                SuperheroService.SUPERHEROES_NOT_FOUND_MSG);
    }

    @Test
    @DisplayName("Get a superhero converted to Dto by id")
    void testRetrieveSuperhero() {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));
        SuperheroDto expected = SuperheroDto.fromDomain(superhero);
        SuperheroDto actual = subject.retrieveSuperhero(1L);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Get a superhero throws not found, because not exists")
    void testRetrieveSuperheroThrowsNotFound() {
        given(superheroRepository.findById(1L)).willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.retrieveSuperhero(1L));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).containsSequence(
                SuperheroService.SUPERHERO_NOT_FOUND_MSG);
    }

    @Test
    @DisplayName("Add superhero returns Dto")
    void testAddSuperheroReturnsDto() {
        given(passwordEncoder.encode(anyString())).willReturn("bar");

        SuperheroDto expected = SuperheroDto.fromDomain(superhero);
        given(superheroRepository.save(any(Superhero.class))).willReturn(superhero);

        SuperheroDto actual = subject.addSuperhero(expected);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    @DisplayName("Add superhero null input throws bad request")
    void testAddSuperheroThrowsBadRequest() {
        ApiException exception = assertThrows(ApiException.class,
                () -> subject.addSuperhero(null));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage()).containsSequence(
                SuperheroService.SUPERHERO_NOT_CREATED_MSG);
    }

    @ParameterizedTest
    @MethodSource("updateFieldByField")
    @DisplayName("Update superhero field by field")
    void testUpdateSuperheroUpdates(SuperheroPatchDto givenUpdate, Superhero expected) {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));

        SuperheroDto updatedSuperhero = subject.changeSuperhero(1L, givenUpdate);

        assertThat(expected.getAlias()).isEqualTo(updatedSuperhero.alias());
        assertThat(expected.getRealName()).isEqualTo(updatedSuperhero.realName());
        assertThat(expected.getDateOfBirth()).isEqualTo(updatedSuperhero.dateOfBirth());
        assertThat(expected.getGender()).isEqualTo(updatedSuperhero.gender());
        assertThat(expected.getOccupation()).isEqualTo(updatedSuperhero.occupation());
    }

    @Test
    @DisplayName("Update superhero throws not found, because not exists")
    void testUpdateSuperheroThrowsNotFound() {
        given(superheroRepository.findById(1L)).willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.changeSuperhero(1L,
                        new SuperheroPatchDto(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty())));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).containsSequence(
                SuperheroService.SUPERHERO_NOT_FOUND_MSG);
    }

    @Test
    @DisplayName("Mark superhero soft deleted")
    void testMarkSuperheroSoftDeleted() {
        given(superheroRepository.findById(1L)).willReturn(Optional.of(superhero));

        Superhero expected = superhero;
        expected.setDeleted(true);
        expected.getUser().setDeleted(true);
        given(superheroRepository.save(any(Superhero.class))).willReturn(superhero);

        SuperheroDto actual = subject.markSuperheroAsDeleted(1L);

        assertThat(actual).usingRecursiveComparison().isEqualTo(SuperheroDto.fromDomain(expected));
    }

    @Test
    @DisplayName("Mark superhero soft deleted throws not found, because not exists")
    void testMarkSuperheroSoftDeletedThrowsNotFound() {
        given(superheroRepository.findById(1L)).willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.markSuperheroAsDeleted(1L));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).containsSequence(
                SuperheroService.SUPERHERO_NOT_FOUND_MSG);
    }
}