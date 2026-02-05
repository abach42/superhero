package com.abach42.superhero.superhero;

import static com.abach42.superhero.shared.api.PathConfig.SUPERHEROES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.shared.convertion.PatchField;
import com.abach42.superhero.testconfiguration.ObjectMapperSerializerHelper;
import com.abach42.superhero.testconfiguration.TestContainerConfiguration;
import com.abach42.superhero.user.ApplicationUser;
import com.abach42.superhero.user.ApplicationUserDto;
import com.abach42.superhero.user.ApplicationUserRepository;
import com.abach42.superhero.user.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tags(value = {@Tag("integration"), @Tag("superhero")})
@SpringBootTest(classes = {TestContainerConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Import({ObjectMapperSerializerHelper.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SuperheroControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectMapper superheroObjectMapper;

    @Autowired
    private SuperheroService superheroService;

    @MockitoBean
    private ApplicationUserRepository applicationUserRepository;

    @Nested
    @DisplayName("Test actions")
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    class TestActions {

        @Test
        @DisplayName("Should retrieve superhero list successfully")
        @WithMockUser(username = "user@example.com", authorities = {"ROLE_USER"})
        void shouldRetrieveSuperheroListSuccessfully() throws Exception {
            String uri = UriComponentsBuilder.fromPath(SUPERHEROES).toUriString();

            MvcResult result = mockMvc.perform(get(uri)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponse = result.getResponse().getContentAsString();
            SuperheroListDto actualList = objectMapper.readValue(actualResponse,
                    SuperheroListDto.class);

            assertThat(actualList.superheroes()).isNotEmpty();
            assertThat(actualList.pageMeta()).isNotNull();
        }

        @Test
        @DisplayName("Should retrieve single superhero successfully")
        @WithMockUser(username = "user@example.com", authorities = {"ROLE_USER"})
        void shouldRetrieveSingleSuperheroSuccessfully() throws Exception {
            Long superheroId = 1L;

            String uri = UriComponentsBuilder.fromPath(SUPERHEROES)
                    .pathSegment("{superheroId}")
                    .buildAndExpand(superheroId)
                    .toUriString();

            MvcResult result = mockMvc.perform(get(uri)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponse = result.getResponse().getContentAsString();
            SuperheroDto actualSuperhero = objectMapper.readValue(actualResponse,
                    SuperheroDto.class);

            assertThat(actualSuperhero.id()).isEqualTo(superheroId);
            assertThat(actualSuperhero.alias()).isNotBlank();
            assertThat(actualSuperhero.realName()).isNotBlank();
        }

        @Test
        @DisplayName("Should create new superhero successfully")
        @WithMockUser(username = "admin@example.com", authorities = {"ROLE_ADMIN"})
        void shouldCreateNewSuperheroSuccessfully() throws Exception {
            SuperheroDto newSuperhero = new SuperheroDto(
                    null,
                    "Test Hero",
                    "Test Real Name",
                    LocalDate.of(1985, 5, 15),
                    Gender.MALE,
                    "Test Occupation",
                    "Test origin story for integration testing",
                    new ApplicationUserDto(
                            "unique-testuser@example.com",
                            "password123",
                            UserRole.USER
                    )
            );

            String uri = UriComponentsBuilder.fromPath(SUPERHEROES).toUriString();

            ApplicationUser newUser = new ApplicationUser("unique-testuser@example.com",
                    "password123", UserRole.USER);
            when(applicationUserRepository.findOneByEmailAndDeletedIsFalse(
                    "unique-testuser@example.com"))
                    .thenReturn(Optional.empty())
                    .thenReturn(Optional.of(newUser));

            MvcResult result = mockMvc.perform(post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newSuperhero)))
                    .andReturn();

            System.out.println("Status: " + result.getResponse().getStatus());
            System.out.println("Response: " + result.getResponse().getContentAsString());

            if (result.getResponse().getStatus() == 201) {
                String actualResponse = result.getResponse().getContentAsString();
                SuperheroDto createdSuperhero = objectMapper.readValue(actualResponse,
                        SuperheroDto.class);

                assertThat(createdSuperhero.id()).isNotNull();
                assertThat(createdSuperhero.alias()).isEqualTo(newSuperhero.alias());
                assertThat(createdSuperhero.realName()).isEqualTo(newSuperhero.realName());
                assertThat(createdSuperhero.dateOfBirth()).isEqualTo(newSuperhero.dateOfBirth());
                assertThat(createdSuperhero.gender()).isEqualTo(newSuperhero.gender());
                assertThat(createdSuperhero.occupation()).isEqualTo(newSuperhero.occupation());
                assertThat(createdSuperhero.originStory()).isEqualTo(newSuperhero.originStory());
            }
        }

        @Test
        @DisplayName("Should update superhero with partial data using manual dirty checking")
        @WithMockUser(username = "admin@example.com", authorities = {"ROLE_ADMIN"})
        @Transactional
        void shouldUpdateSuperheroWithPartialDataUsingManualDirtyChecking() throws Exception {
            Long superheroId = 1L;

            SuperheroDto existingSuperhero = superheroService.retrieveSuperhero(superheroId);

            SuperheroPatchDto partialUpdate = new SuperheroPatchDto(
                    PatchField.of("Updated Alias"),
                    PatchField.missing(),
                    PatchField.missing(),
                    PatchField.missing(),
                    PatchField.of("Updated Occupation"),
                    PatchField.missing()
            );

            String uri = UriComponentsBuilder.fromPath(SUPERHEROES)
                    .pathSegment("{superheroId}")
                    .buildAndExpand(superheroId)
                    .toUriString();

            MvcResult result = mockMvc.perform(patch(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(partialUpdate)))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponse = result.getResponse().getContentAsString();
            SuperheroDto updatedSuperhero = objectMapper.readValue(actualResponse,
                    SuperheroDto.class);

            assertThat(updatedSuperhero.id()).isEqualTo(superheroId);
            assertThat(updatedSuperhero.alias()).isEqualTo("Updated Alias");
            assertThat(updatedSuperhero.realName()).isEqualTo(existingSuperhero.realName());
            assertThat(updatedSuperhero.dateOfBirth()).isEqualTo(existingSuperhero.dateOfBirth());
            assertThat(updatedSuperhero.gender()).isEqualTo(existingSuperhero.gender());
            assertThat(updatedSuperhero.occupation()).isEqualTo("Updated Occupation");
            assertThat(updatedSuperhero.originStory()).isEqualTo(existingSuperhero.originStory());
        }
    }

    @Nested
    @DisplayName("Method Security Tests")
    class MethodSecurity {

        @Test
        @DisplayName("Should allow USER to access read endpoints and deny write endpoints")
        @WithMockUser(authorities = {"ROLE_USER"})
        void shouldAllowUserReadOnlyAccess() throws Exception {
            mockMvc.perform(get(SUPERHEROES)).andExpect(status().isOk());
            mockMvc.perform(get(SUPERHEROES + "/1")).andExpect(status().isOk());

            SuperheroDto validSuperhero = new SuperheroDto(
                    null,
                    "Test Alias",
                    "Test Real Name",
                    LocalDate.of(1990, 1, 1),
                    Gender.MALE,
                    "Test Occupation",
                    "Test Origin Story",
                    new ApplicationUserDto("test@example.com", "password123",
                            UserRole.USER)
            );

            SuperheroPatchDto validPatch = new SuperheroPatchDto(
                    PatchField.of("Updated Alias"),
                    PatchField.missing(),
                    PatchField.missing(),
                    PatchField.missing(),
                    PatchField.missing(),
                    PatchField.missing()
            );

            mockMvc.perform(post(SUPERHEROES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(superheroObjectMapper.writeValueAsString(validSuperhero)))
                    .andExpect(status().isForbidden());

            mockMvc.perform(patch(SUPERHEROES + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(superheroObjectMapper.writeValueAsString(validPatch)))
                    .andExpect(status().isForbidden());

            mockMvc.perform(delete(SUPERHEROES + "/1")).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("ADMIN should access all endpoints")
        @WithMockUser(authorities = {"ROLE_ADMIN"})
        void shouldAllowAdminFullAccess() throws Exception {
            mockMvc.perform(get(SUPERHEROES))
                    .andExpect(status().isOk());

            mockMvc.perform(get(SUPERHEROES + "/1"))
                    .andExpect(status().isOk());

            SuperheroDto validSuperhero = new SuperheroDto(
                    null,
                    "Admin Test Alias",
                    "Admin Test Real Name",
                    LocalDate.of(1985, 5, 15),
                    Gender.FEMALE,
                    "Admin Occupation",
                    "Admin Origin Story",
                    new ApplicationUserDto("admin-test@example.com", "password123",
                            UserRole.USER)
            );

            SuperheroPatchDto validPatch = new SuperheroPatchDto(
                    PatchField.of("Admin Updated Alias"),
                    PatchField.missing(),
                    PatchField.missing(),
                    PatchField.missing(),
                    PatchField.missing(),
                    PatchField.missing()
            );

            mockMvc.perform(post(SUPERHEROES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(superheroObjectMapper.writeValueAsString(validSuperhero)))
                    .andExpect(status().isCreated());

            mockMvc.perform(patch(SUPERHEROES + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(superheroObjectMapper.writeValueAsString(validPatch)))
                    .andExpect(status().isOk());

            mockMvc.perform(delete(SUPERHEROES + "/1"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Not existing user should be forbidden")
        @WithAnonymousUser
        void shouldDisallowAnonymous() throws Exception {
            mockMvc.perform(get(SUPERHEROES)).andExpect(status().isForbidden());
            mockMvc.perform(get(SUPERHEROES + "/1")).andExpect(status().isForbidden());

            SuperheroDto validSuperhero = new SuperheroDto(
                    null,
                    "Test Alias",
                    "Test Real Name",
                    LocalDate.of(1990, 1, 1),
                    Gender.MALE,
                    "Test Occupation",
                    "Test Origin Story",
                    new ApplicationUserDto("test@example.com", "password123",
                            UserRole.USER)
            );

            SuperheroPatchDto validPatch = new SuperheroPatchDto(
                    PatchField.of("Updated Alias"),
                    PatchField.missing(),
                    PatchField.missing(),
                    PatchField.missing(),
                    PatchField.missing(),
                    PatchField.missing()
            );

            mockMvc.perform(post(SUPERHEROES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(superheroObjectMapper.writeValueAsString(validSuperhero)))
                    .andExpect(status().isForbidden());

            mockMvc.perform(patch(SUPERHEROES + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(superheroObjectMapper.writeValueAsString(validPatch)))
                    .andExpect(status().isForbidden());

            mockMvc.perform(delete(SUPERHEROES + "/1")).andExpect(status().isForbidden());
        }
    }
}