package com.abach42.superhero.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.config.api.PathConfig;
import com.abach42.superhero.config.security.SecuredAdmin;
import com.abach42.superhero.config.security.SecuredUser;
import com.abach42.superhero.configuration.ObjectMapperSerializerHelper;
import com.abach42.superhero.configuration.TestContainerConfiguration;
import com.abach42.superhero.configuration.TestDataConfiguration;
import com.abach42.superhero.dto.ErrorDto;
import com.abach42.superhero.dto.SuperheroDto;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.repository.SuperheroRepository;
import com.abach42.superhero.service.SuperheroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;

/*
 * Integration test with real database and mock client
 *
 * * Validations: Null fails
 * * Validations: Non null fails (missing field, missing payload)
 * * CRUD not found fails
 * * Write for admin only test
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SuperheroControllerIntegrationTest {

    private final static String PATH = PathConfig.SUPERHEROES;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SuperheroRepository superheroRepository;

    private Superhero superhero;

    //unable to mock on layer
    private RequestPostProcessor allAuthorities = SecurityMockMvcRequestPostProcessors.jwt()
            .authorities(new SimpleGrantedAuthority(SecuredAdmin.ROLE_ADMIN),
                    new SimpleGrantedAuthority(SecuredUser.ROLE_USER));

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectMapperSerializerHelper superheroDtoSerializer;

    @BeforeEach
    public void setUp() {
        this.superhero = TestDataConfiguration.getSuperheroStub();
    }

    private ErrorDto getErrorDtoFromResultPayload(MvcResult mvcResult) throws IOException {
        ErrorDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                ErrorDto.class);
        return actual;
    }

    @Test
    @DisplayName("POST " + PATH + " (mockmvc + db) create superhero fails when missing field")
    public void testCreateSuperheroFailsWhenMissingFieldInPayload() throws Exception {
        Superhero failingSuperhero = superhero;
        failingSuperhero.setAlias(null);

        mockMvc.perform(
                        post(PATH)
                                .content(superheroDtoSerializer.get()
                                        .writeValueAsString(SuperheroDto.fromDomain(failingSuperhero)))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST " + PATH + " (mockmvc + db) create superhero fails when missing payload")
    public void testCreateSuperheroFailsWhenEmptyPayload() throws Exception {
        mockMvc.perform(
                        post(PATH)
                                .content(objectMapper.writeValueAsString(null))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(
            "POST " + PATH + " (mockmvc + db) create superhero skill profile returns created link")
    public void testCreateSuperheroSkillProfileReturnsCreatedLink() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        post(PATH)
                                .content(superheroDtoSerializer.get().writeValueAsString(
                                        TestDataConfiguration.getSuperheroDtoStubWithPassword()))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(allAuthorities))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        SuperheroDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SuperheroDto.class);

        String locationHeader = mvcResult.getResponse().getHeader("Location");
        assertThat(locationHeader).endsWith(PATH + "/" + actual.id());
    }

    @Test
    @DisplayName("PUT " + PATH + "/1 (mockmvc + db) update superhero fails when missing field")
    public void testUpdateSuperheroFailsWhenMissingFieldInPayload() throws Exception {
        mockMvc.perform(
                        put(PATH + "/" + 1)
                                .content(objectMapper.writeValueAsString(null))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT " + PATH + "/1 (mockmvc + db) update superhero fails when not found")
    public void testUpdateSuperheroFailsWhenNoSuperheroFound() throws Exception {
        superheroRepository.deleteAll();

        MvcResult mvcResult = mockMvc.perform(
                        put(PATH + "/" + 1)
                                .content(superheroDtoSerializer.get()
                                        .writeValueAsString(SuperheroDto.fromDomain(superhero)))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(allAuthorities))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorDto actual = getErrorDtoFromResultPayload(mvcResult);
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        SuperheroService.SUPERHERO_NOT_FOUND_MSG + 1,
                        PATH + "/" + 1));
    }

    @Test
    @DisplayName("DELETE " + PATH + "/1 (mockmvc + db) soft delete superhero fails when not found")
    public void testSoftDeleteSuperheroFailsWhenNotFound() throws Exception {
        superheroRepository.deleteAll();

        MvcResult mvcResult = mockMvc.perform(
                        delete(PATH + "/" + 1)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(allAuthorities))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorDto actual = getErrorDtoFromResultPayload(mvcResult);
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        SuperheroService.SUPERHERO_NOT_FOUND_MSG + 1,
                        PATH + "/" + 1));
    }

    @Nested
    @NestedTestConfiguration(EnclosingConfiguration.OVERRIDE)
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    @DisplayName("Test without database change")
    class NoDatabaseChangeTest {

        @Test
        @DisplayName("GET " + PATH
                + "?page=999 (mockmvc + db) list superheros fails when page number to high")
        public void testListSuperheroesFailsWhenPageNotExists() throws Exception {
            int failPage = 999; // assuming test data have max 998 pages

            MvcResult mvcResult = mockMvc.perform(
                            get(PATH + "?page=" + failPage)
                                    .with(allAuthorities)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnprocessableEntity())
                    .andReturn();

            ErrorDto actual = getErrorDtoFromResultPayload(mvcResult);
            assertThat(actual).usingRecursiveComparison()
                    .isEqualTo(new ErrorDto(HttpStatus.UNPROCESSABLE_ENTITY.value(),
                            HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                            SuperheroService.MAX_PAGE_EXCEEDED_MSG + " Total: 1, requested: "
                                    + failPage + ".",
                            PATH));
        }

        @Test
        @DisplayName("GET " + PATH + " (mockmvc + db) list superheroes fails when not found")
        public void testListSuperherosFailsWhenNoSuperhero() throws Exception {
            superheroRepository.deleteAll();

            MvcResult mvcResult = mockMvc.perform(
                            get(PATH).accept(MediaType.APPLICATION_JSON)
                                    .with(allAuthorities))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn();

            ErrorDto actual = getErrorDtoFromResultPayload(mvcResult);
            assertThat(actual).usingRecursiveComparison()
                    .isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(),
                            HttpStatus.NOT_FOUND.getReasonPhrase(),
                            SuperheroService.SUPERHEROES_NOT_FOUND_MSG, PATH));
        }

        @Test
        @DisplayName("GET " + PATH + "/1 (mockmvc + db) show a superhero fails when not found")
        public void testShowSuperheroFailsWhenNoSuperhero() throws Exception {
            superheroRepository.deleteAll();

            MvcResult mvcResult = mockMvc.perform(
                            get(PATH + "/" + 1)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .with(allAuthorities))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn();

            ErrorDto actual = getErrorDtoFromResultPayload(mvcResult);
            assertThat(actual).usingRecursiveComparison()
                    .isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(),
                            HttpStatus.NOT_FOUND.getReasonPhrase(),
                            SuperheroService.SUPERHERO_NOT_FOUND_MSG + 1, PATH + "/" + 1));
        }
    }

    @Nested
    @NestedTestConfiguration(EnclosingConfiguration.INHERIT)
    @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
    @DisplayName("Test Roles")
    class RolesTest {

        private static Stream<Arguments> adminOnlyProvider() {
            return Stream.of(
                    Arguments.of(Arrays.asList(new SimpleGrantedAuthority(SecuredUser.ROLE_USER)),
                            MockMvcResultMatchers.status().isForbidden()),
                    Arguments.of(Arrays.asList(new SimpleGrantedAuthority(SecuredAdmin.ROLE_ADMIN),
                                    new SimpleGrantedAuthority(SecuredUser.ROLE_USER)),
                            MockMvcResultMatchers.status().is2xxSuccessful()));
        }

        @ParameterizedTest
        @MethodSource("adminOnlyProvider")
        @DisplayName("POST " + PATH + " (mockmvc + db) according to roles")
        public void testPost(Collection<GrantedAuthority> authorities, ResultMatcher status)
                throws Exception {
            mockMvc.perform(
                            request(HttpMethod.POST, PATH)
                                    .with(SecurityMockMvcRequestPostProcessors.jwt()
                                            .authorities(authorities))
                                    .content(superheroDtoSerializer.get()
                                            .writeValueAsString(
                                                    TestDataConfiguration.getSuperheroDtoStubWithPassword()))
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status);
        }

        @ParameterizedTest
        @MethodSource("adminOnlyProvider")
        @DisplayName("PUT " + PATH + "/1 (mockmvc + db) according to roles")
        public void testPut(Collection<GrantedAuthority> authorities, ResultMatcher status)
                throws Exception {
            mockMvc.perform(
                            request(HttpMethod.PUT, PATH + "/" + 1)
                                    .with(SecurityMockMvcRequestPostProcessors.jwt()
                                            .authorities(authorities))
                                    .content(superheroDtoSerializer.get()
                                            .writeValueAsString(SuperheroDto.fromDomain(superhero)))
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status);
        }

        @ParameterizedTest
        @MethodSource("adminOnlyProvider")
        @DisplayName("DELETE " + PATH + "/1 (mockmvc + db) according to roles")
        public void testDelete(Collection<GrantedAuthority> authorities, ResultMatcher status)
                throws Exception {
            mockMvc.perform(
                            request(HttpMethod.DELETE, PATH + "/" + 1)
                                    .with(SecurityMockMvcRequestPostProcessors.jwt()
                                            .authorities(authorities))
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status);
        }

        @Nested
        @NestedTestConfiguration(EnclosingConfiguration.INHERIT)
        @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
        @DisplayName("Test Roles no database change")
        class RolesTestNoDatabaseChange {

            private static Stream<Arguments> bothRolesProvider() {
                return Stream.of(
                        Arguments.of(
                                Arrays.asList(new SimpleGrantedAuthority(SecuredUser.ROLE_USER)),
                                MockMvcResultMatchers.status().is2xxSuccessful()),
                        Arguments.of(
                                Arrays.asList(new SimpleGrantedAuthority(SecuredAdmin.ROLE_ADMIN),
                                        new SimpleGrantedAuthority(SecuredUser.ROLE_USER)),
                                MockMvcResultMatchers.status().is2xxSuccessful()));
            }

            //duplicated test on another layer, just for example here
            @ParameterizedTest
            @MethodSource("bothRolesProvider")
            @DisplayName("GET " + PATH + " (mockmvc + db) according to roles")
            public void testGetAll(Collection<GrantedAuthority> authorities, ResultMatcher status)
                    throws Exception {
                mockMvc.perform(
                                request(HttpMethod.GET, PATH)
                                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                                .authorities(authorities))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status);
            }

            //duplicated test on another layser, just for example here
            @ParameterizedTest
            @MethodSource("bothRolesProvider")
            @DisplayName("GET " + PATH + "/1 (mockmvc + db) according to roles")
            public void testGet(Collection<GrantedAuthority> authorities, ResultMatcher status)
                    throws Exception {
                mockMvc.perform(
                                request(HttpMethod.GET, PATH + "/" + 1)
                                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                                .authorities(authorities))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status);
            }
        }
    }
}