package com.abach42.superhero.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
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

import com.abach42.superhero.config.api.PathConfig;
import com.abach42.superhero.config.security.SecuredAdmin;
import com.abach42.superhero.config.security.SecuredUser;
import com.abach42.superhero.configuration.TestContainerConfiguration;
import com.abach42.superhero.configuration.TestDataConfiguration;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.dto.ErrorDto;
import com.abach42.superhero.entity.dto.SuperheroDto;
import com.abach42.superhero.entity.dto.UserDto;
import com.abach42.superhero.repository.SuperheroRepository;
import com.abach42.superhero.service.SuperheroService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * End to end test with real database and mock client
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SuperheroControllerIntegrationTest {
    private final static String PATH = PathConfig.SUPERHEROES;

    private static final int TOTAL_PAGES = 1;

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

    @BeforeEach
    public void setUp() {
        this.superhero = TestDataConfiguration.DUMMY_SUPERHERO;
    }

    @Nested
    @NestedTestConfiguration(EnclosingConfiguration.OVERRIDE)
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    @DisplayName("Test without database change")
    class NoDatabaseChangeTest {
        private Superhero superhero;

        @BeforeEach
        public void setUp() {
            this.superhero = TestDataConfiguration.DUMMY_SUPERHERO;
        }

        @Test
        @DisplayName("GET " + PATH + "?page=999 (mockmvc + db) get all superheros page number too high")
        public void testGetAllSuperherosFailsWhenPageNotExists() throws Exception {
            int failPage = 999; // assuming test data have max 998 pages

            MvcResult mvcResult = mockMvc.perform(
                    get(PATH + "?page=" + failPage)
                            .with(allAuthorities) 
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnprocessableEntity())
                    .andReturn();

            ErrorDto actual = getErrorDtoFromResutPayload(mvcResult);
            assertThat(actual).usingRecursiveComparison()
                    .isEqualTo(new ErrorDto(HttpStatus.UNPROCESSABLE_ENTITY.value(),
                            HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                            SuperheroService.MAX_PAGE_EXEEDED_MSG + " Total: " + TOTAL_PAGES + ", requested: " + failPage + ".",
                            PATH));
        }

        @Test
        @DisplayName("POST " + PATH + " (mockmvc + db) add new superhero fails when missing field")
        public void testAddSuperheroFailsWhenMissingFieldInPayload() throws Exception {
            Superhero failingSuperhero = superhero;
            failingSuperhero.setAlias(null);

            mockMvc.perform(
                        post(PATH)
                                .content(objectMapper.writeValueAsString(SuperheroDto.fromDomain(failingSuperhero)))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(SecurityMockMvcRequestPostProcessors.jwt()))
                    .andDo(print())
                    .andExpect(status().isUnprocessableEntity());
        }
    
        @Test
        @DisplayName("POST " + PATH + " (mockmvc + db) add new superhero fails when missing")
        public void testAddSuperheroFailsWhenEmptyPayload() throws Exception {
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
        @DisplayName("PUT " + PATH  + "/1 (mockmvc + db) update superhero fails when missing field")
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
    }

    @Test
    @DisplayName("GET " + PATH + " (mockmvc + db) get all supeheroes not found")
    public void testGetAllSuperherosFailsWhenNoSuperhero() throws Exception {
        superheroRepository.deleteAll();

        MvcResult mvcResult = mockMvc.perform(
                    get(PATH).accept(MediaType.APPLICATION_JSON)
                            .with(allAuthorities))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorDto actual = getErrorDtoFromResutPayload(mvcResult);
        assertThat(actual).usingRecursiveComparison().isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(), SuperheroService.SUPERHEROES_NOT_FOUND_MSG, PATH));
    }

    private ErrorDto getErrorDtoFromResutPayload(MvcResult mvcResult)
            throws JsonProcessingException, JsonMappingException, UnsupportedEncodingException {
        ErrorDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDto.class);
        return actual;
    }
    
    @Test
    @DisplayName("GET "+ PATH + "/1 (mockmvc + db) get a superhero not found")
    public void testGetSuperheroFailsWhenNoSuperhero() throws Exception {
        superheroRepository.deleteAll();

        MvcResult mvcResult = mockMvc.perform(
                    get(PATH + "/" + 1)
                            .accept(MediaType.APPLICATION_JSON)
                            .with(allAuthorities))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorDto actual = getErrorDtoFromResutPayload(mvcResult);
        assertThat(actual).usingRecursiveComparison().isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(), SuperheroService.SUPERHERO_NOT_FOUND_MSG + 1, PATH + "/" + 1));
    }

    @Test
    @DisplayName("PUT "+ PATH + "/1 (mockmvc + db) update superhero not found")
    public void testUpdateSuperheroFailsWhenNoSuperheroFound() throws Exception {
        superheroRepository.deleteAll();

        MvcResult mvcResult = mockMvc.perform(
                    put(PATH + "/" + 1)
                            .content(objectMapper.writeValueAsString(SuperheroDto.fromDomain(superhero)))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(allAuthorities))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorDto actual = getErrorDtoFromResutPayload(mvcResult);
        assertThat(actual).usingRecursiveComparison().isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(), SuperheroService.SUPERHERO_NOT_FOUND_MSG + 1,
                PATH + "/" + 1));
    }

    @Test
    @DisplayName("DELETE "+ PATH + "/1 (mockmvc + db) soft delete superhero not found")
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

        ErrorDto actual = getErrorDtoFromResutPayload(mvcResult);
        assertThat(actual).usingRecursiveComparison().isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(), SuperheroService.SUPERHERO_NOT_FOUND_MSG + 1,
                PATH + "/" + 1));
    }

    @Nested
    @NestedTestConfiguration(EnclosingConfiguration.INHERIT)
    @DisplayName("Test Roles")
    class RolesTest {

        private Superhero superhero;
        
        @BeforeEach
        public void setUp() {
            this.superhero = TestDataConfiguration.DUMMY_SUPERHERO;
        }

        @ParameterizedTest
        @MethodSource("bothRolesProvider")
        @DisplayName("GET " + PATH + " (mockmvc + db) according to roles")
        public void testGetAll(Collection<GrantedAuthority> authorities, ResultMatcher status) throws Exception {
            mockMvc.perform(
                        request(HttpMethod.GET, PATH)
                                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(authorities))
                                .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status);
        }

        @ParameterizedTest
        @MethodSource("bothRolesProvider")
        @DisplayName("GET " + PATH + "/1 (mockmvc + db) according to roles")
        public void testGet(Collection<GrantedAuthority> authorities, ResultMatcher status) throws Exception {
            mockMvc.perform(
                        request(HttpMethod.GET, PATH + "/" + 1)
                                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(authorities))
                                .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status);
        }

        @ParameterizedTest
        @MethodSource("adminOnlyProvider")
        @DisplayName("POST " + PATH + " (mockmvc + db) according to roles")
        public void testPost(Collection<GrantedAuthority> authorities, ResultMatcher status) throws Exception {
            mockMvc.perform(
                        request(HttpMethod.POST, PATH)
                                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(authorities))
                                .content(objectMapper.writeValueAsString(new SuperheroDto(null, "foo", "foo", LocalDate.of(1970,1,1), "foo",
                                                 "foo", "foo", new UserDto("foo", "bar", "USER"))))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status);
        }

        @ParameterizedTest
        @MethodSource("adminOnlyProvider")
        @DisplayName("PUT " + PATH + "/1 (mockmvc + db) according to roles")
        public void testPut(Collection<GrantedAuthority> authorities, ResultMatcher status) throws Exception {
            mockMvc.perform(
                        request(HttpMethod.PUT, PATH + "/" + 1)
                                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(authorities))
                                .content(objectMapper.writeValueAsString(SuperheroDto.fromDomain(superhero)))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status);
        }

        @ParameterizedTest
        @MethodSource("adminOnlyProvider")
        @DisplayName("DELETE " + PATH + "/1 (mockmvc + db) according to roles")
        public void testDelete(Collection<GrantedAuthority> authorities, ResultMatcher status) throws Exception {
            mockMvc.perform(
                        request(HttpMethod.DELETE, PATH + "/" + 1)
                                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(authorities))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status);
        }

        private static Stream<Arguments> adminOnlyProvider() {
            return Stream.of(
                    Arguments.of(Arrays.asList(new SimpleGrantedAuthority(SecuredUser.ROLE_USER)),
                            MockMvcResultMatchers.status().isForbidden()),
                    Arguments.of(Arrays.asList(new SimpleGrantedAuthority(SecuredAdmin.ROLE_ADMIN), new SimpleGrantedAuthority(SecuredUser.ROLE_USER)),
                            MockMvcResultMatchers.status().is2xxSuccessful()));
        }

        private static Stream<Arguments> bothRolesProvider() {
            return Stream.of(
                    Arguments.of(Arrays.asList(new SimpleGrantedAuthority(SecuredUser.ROLE_USER)),
                            MockMvcResultMatchers.status().is2xxSuccessful()),
                    Arguments.of(Arrays.asList(new SimpleGrantedAuthority(SecuredAdmin.ROLE_ADMIN), new SimpleGrantedAuthority(SecuredUser.ROLE_USER)),
                            MockMvcResultMatchers.status().is2xxSuccessful()));
        }
    }
}