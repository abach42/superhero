package com.abach42.superhero.superhero;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.abach42.superhero.shared.api.ApiException;
import com.abach42.superhero.shared.convertion.PatchField;
import com.abach42.superhero.testconfiguration.TestContainerConfiguration;
import com.abach42.superhero.user.ApplicationUserDto;
import com.abach42.superhero.user.ApplicationUserRepository;
import com.abach42.superhero.user.UserRole;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tags(value = {@Tag("integration"), @Tag("superhero")})
@SpringBootTest(classes = {TestContainerConfiguration.class})
@Testcontainers
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SuperheroServiceIntegrationTest {

    @Autowired
    private SuperheroService superheroService;

    @MockitoBean
    private ApplicationUserRepository applicationUserRepository;

    @Test
    @DisplayName("Should test manual dirty checking with transactional service method")
    @Transactional
    void shouldTestManualDirtyCheckingWithTransactionalServiceMethod() throws ApiException {
        Long superheroId = 1L;

        SuperheroDto originalSuperhero = superheroService.retrieveSuperhero(superheroId);

        SuperheroPatchDto partialUpdate = new SuperheroPatchDto(
                PatchField.of("Service Updated Alias"),
                PatchField.missing(),
                PatchField.missing(),
                PatchField.missing(),
                PatchField.missing(),
                PatchField.of("Service Updated Origin Story")
        );

        SuperheroDto updatedSuperhero = superheroService.changeSuperhero(superheroId, partialUpdate);

        assertThat(updatedSuperhero.alias()).isEqualTo("Service Updated Alias");
        assertThat(updatedSuperhero.realName()).isEqualTo(originalSuperhero.realName());
        assertThat(updatedSuperhero.dateOfBirth()).isEqualTo(originalSuperhero.dateOfBirth());
        assertThat(updatedSuperhero.gender()).isEqualTo(originalSuperhero.gender());
        assertThat(updatedSuperhero.occupation()).isEqualTo(originalSuperhero.occupation());
        assertThat(updatedSuperhero.originStory()).isEqualTo("Service Updated Origin Story");

        SuperheroDto persistedSuperhero = superheroService.retrieveSuperhero(superheroId);
        assertThat(persistedSuperhero.alias()).isEqualTo("Service Updated Alias");
        assertThat(persistedSuperhero.originStory()).isEqualTo("Service Updated Origin Story");
    }

    @Test
    @DisplayName("Should mark superhero as deleted successfully")
    @WithMockUser(username = "admin@example.com", authorities = {"ROLE_ADMIN"})
    void shouldMarkSuperheroAsDeletedSuccessfully() throws Exception {
        Long superheroId = 1L;

        SuperheroDto deletedSuperhero = superheroService.markSuperheroAsDeleted(superheroId);

        assertThat(deletedSuperhero.id()).isEqualTo(superheroId);
    }

    @Test
    @DisplayName("Should throw exception when superhero not found")
    void shouldThrowExceptionWhenSuperheroNotFound() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> superheroService.retrieveSuperhero(nonExistentId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(
                        SuperheroService.SUPERHERO_NOT_FOUND_MSG + nonExistentId);
    }

    @Test
    @DisplayName("Should throw exception when creating superhero with duplicate email")
    void shouldThrowExceptionWhenCreatingSuperheroWithDuplicateEmail() {
        SuperheroDto duplicateEmailSuperhero = new SuperheroDto(
                null,
                "Duplicate Hero",
                "Duplicate Name",
                LocalDate.of(1990, 1, 1),
                Gender.MALE,
                "Test Occupation",
                "Test Origin Story",
                new ApplicationUserDto("user@example.com", "password123", UserRole.USER)
        );

        assertThatThrownBy(() -> superheroService.addSuperhero(duplicateEmailSuperhero))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(SuperheroService.SUPERHERO_NOT_CREATED_MSG_CONSTRAINT);
    }

    @Test
    @DisplayName("Should return empty list when no superheroes found for ids")
    void shouldReturnEmptyListWhenNoSuperheroesFoundForIds() {
        java.util.Set<Long> nonExistentIds = java.util.Set.of(999L, 1000L);
        java.util.List<Superhero> result = superheroService.retrieveSuperheroesInList(nonExistentIds);
        assertThat(result).isEmpty();
    }
}
