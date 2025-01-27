package com.abach42.superhero.functional.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.WebApplicationContext;

import com.abach42.superhero.config.api.OnCreate;
import com.abach42.superhero.config.api.PathConfig;
import com.abach42.superhero.configuration.ObjectMapperSerializerHelper;
import com.abach42.superhero.configuration.TestDataConfiguration;
import com.abach42.superhero.controller.SuperheroController;
import com.abach42.superhero.dto.SuperheroDto;
import com.abach42.superhero.dto.SuperheroListDto;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.service.SuperheroService;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Mocked rest client, regarding validation, mocked database
 * 
 * * Fails no auth
 * * CRUD succeeds 
 * * Write fails on missing filed
 */
@WebMvcTest(SuperheroController.class)
@ContextConfiguration
@WebAppConfiguration
@Import(ObjectMapperSerializerHelper.class)
@WithMockUser
public class SuperheroControllerTest {
    private final static String PATH = PathConfig.SUPERHEROES;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;
    
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SuperheroService superheroService;

    private SuperheroDto superheroDtoStub;

    @Autowired
    private ObjectMapperSerializerHelper superheroDtoSerializer;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                        .webAppContextSetup(context)
                        .apply(springSecurity()) 
                        .build();
                                
        this.superheroDtoStub = TestDataConfiguration.getSuperheroDtoStub();
    }

    @Test
    @DisplayName("GET " + PATH + " returns first page of superheroes")
    public void testListSuperheroes() throws Exception {
        SuperheroListDto expected = SuperheroListDto.fromPage(new PageImpl<>(List.of(superheroDtoStub),
                PageRequest.ofSize(1), 1L), 1L);

        given(superheroService.retrieveSuperheroList(null)).willReturn(expected);

        MvcResult mvcResult = mockMvc.perform(
                        get(PATH)
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                .andReturn();

        SuperheroListDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SuperheroListDto.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("GET " + PATH + "/0" + " returns a superhero")
    public void testShowSuperhero() throws Exception {
        SuperheroDto expected = superheroDtoStub;

        given(superheroService.retrieveSuperhero(0L)).willReturn(expected);

        MvcResult mvcResult = mockMvc.perform(
                        get(PATH + "/" + 0)
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                .andReturn();

        SuperheroDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SuperheroDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("POST " + PATH + " results created")
    public void testCreateSuperhero() throws Exception {
        SuperheroDto input = TestDataConfiguration.getSuperheroDtoStubWithPassword();

        given(superheroService.addSuperhero(any(SuperheroDto.class))).willReturn(input);

        MvcResult mvcResult = mockMvc.perform(
                        post(PATH)
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(superheroDtoSerializer.get().writeValueAsString(input))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isCreated())
                .andReturn();

        SuperheroDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SuperheroDto.class);

        assertThat(actual.user().password()).isNull();

        String locationHeader = mvcResult.getResponse().getHeader("Location");
        assertThat(locationHeader).isNotNull();
    }

    /*
     * Info: This test expected to fail runs here, because on WebMvcTest/mockMvc-level 
     * jakarta validation constraint IS executed, 
     * and on usage of validation *groups* it is executed IF annotation is 
     * related directly to method parameter, and NOT as usual above method. 
     */
    @Test
    @DisplayName("POST " + PATH + " returns 422 on missing field in payload")
    @Validated(OnCreate.class)
    public void testCreateSuperheroFailsOnMissingField() throws Exception {
        SuperheroDto failedSuperheroDto = TestDataConfiguration.getSuperheroDtoStubEmpty(); 
        
        mockMvc.perform(
                post(PATH)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .content(superheroDtoSerializer.get().writeValueAsString(failedSuperheroDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("PUT " + PATH + "/" + 0 + " update updates only changed fields")
    public void testUpdateSuperhero() throws Exception {
        Superhero superheroStub = TestDataConfiguration.getSuperheroStub();
        superheroStub.setRealName("changed");
        SuperheroDto expected = SuperheroDto.fromDomain(superheroStub);

        Superhero inputSuperhero = TestDataConfiguration.getSuperheroStub();
        inputSuperhero.setAlias(null);
        inputSuperhero.setRealName("changed");
        
        SuperheroDto input = SuperheroDto.fromDomain(inputSuperhero);

        given(superheroService.changeSuperhero(anyLong(), any(SuperheroDto.class))).willReturn(expected);

        MvcResult mvcResult = mockMvc.perform(
                        put(PATH + "/" + 0)
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(superheroDtoSerializer.get().writeValueAsString(input))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                .andReturn();

        SuperheroDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SuperheroDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Controller action to soft delete superhero result ok")
    public void testSoftDeleteSuperhero() throws Exception {
        given(superheroService.markSuperheroAsDeleted(anyLong())).willReturn(superheroDtoStub);

        MvcResult mvcResult = mockMvc.perform(
                        delete(PATH + "/" + 0)
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(superheroDtoSerializer.get().writeValueAsString(superheroDtoStub))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                .andReturn();

        SuperheroDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SuperheroDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(superheroDtoStub);
    }

    @ParameterizedTest
    @MethodSource("endpointProvider")
    @DisplayName("fails w/o JWT")
    @WithAnonymousUser
    public void testAnonymousFails(HttpMethod method, String path, ResultMatcher status) throws Exception {
        mockMvc.perform(
                        request(method, path)
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status);
    }

    private static Stream<Arguments> endpointProvider() {
        return Stream.of(
                Arguments.of(HttpMethod.GET, PATH, MockMvcResultMatchers.status().isUnauthorized()),
                Arguments.of(HttpMethod.GET, PATH + "/" + 0, MockMvcResultMatchers.status().isUnauthorized()),
                Arguments.of(HttpMethod.POST, PATH, MockMvcResultMatchers.status().isForbidden()),
                Arguments.of(HttpMethod.PUT, PATH + "/" + 0, MockMvcResultMatchers.status().isForbidden()),
                Arguments.of(HttpMethod.DELETE, PATH + "/" + 0, MockMvcResultMatchers.status().isForbidden())
        );
    }
}