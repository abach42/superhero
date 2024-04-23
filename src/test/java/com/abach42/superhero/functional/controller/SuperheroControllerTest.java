package com.abach42.superhero.functional.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.annotation.Validated;

import com.abach42.superhero.config.OnCreate;
import com.abach42.superhero.config.PathConfig;
import com.abach42.superhero.controller.SuperheroController;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.dto.ErrorDetailedDto;
import com.abach42.superhero.entity.dto.ErrorDto;
import com.abach42.superhero.entity.dto.SuperheroDto;
import com.abach42.superhero.entity.dto.SuperheroListDto;
import com.abach42.superhero.exception.ApiException;
import com.abach42.superhero.service.SuperheroService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;


@WebMvcTest(SuperheroController.class)
public class SuperheroControllerTest {
    private final static String PATH = PathConfig.SUPERHEROES;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SuperheroService superheroService;

    private Superhero superhero;

    private SuperheroDto superheroDto;

    @BeforeEach
    public void setUp() {
        superhero = new Superhero("foo", "bar", LocalDate.of(1917, 1, 1), "Male", "foo", "foo");
        superheroDto = SuperheroDto.fromDomain(superhero);
    }
    @Test
    @DisplayName("Controller action to get superheroes returns first page of superheroes")
    public void testGetAllSuperheros() throws Exception {
        SuperheroListDto expected = SuperheroListDto.fromPage(new PageImpl<>(List.of(superheroDto),
                PageRequest.ofSize(1), 1L), 1L);

        given(superheroService.getAllSuperheros(null)).willReturn(expected);

        MvcResult mvcResult = mockMvc.perform(
                        get(PATH).accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                .andReturn();

        SuperheroListDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SuperheroListDto.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    /*@Test
    @DisplayName("Controller action to get superheros returns 404")
    public void testGetAllSuperherosReturns404() throws Exception {
        given(superheroService.getAllSuperheros(anyInt()))
                        .willThrow(new ApiException(HttpStatus.NOT_FOUND, null));

        MvcResult mvcResult = mockMvc.perform(
                        get(PATH).accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isUnprocessableEntity())
                .andReturn();

        ErrorDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                ErrorDto.class);
        assertThat(actual).isInstanceOf(ErrorDto.class);
    }*/

    /*@Test
    @DisplayName("Controller action to get superheros returns 422")
    public void testGetAllSuperherosRetunrs422() throws Exception {

        given(superheroService.getAllSuperheros(2)).willReturn(null);

        MvcResult mvcResult = mockMvc.perform(
                        get(PATH).accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isNotFound())
                .andReturn();

        ErrorDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                ErrorDto.class);
        assertThat(actual).isInstanceOf(ErrorDto.class);
    }*/

    

    @Test
    @DisplayName("Controller action to get superhero returns a superhero")
    public void testGetSuperhero() throws Exception {
        SuperheroDto expected = superheroDto;

        given(superheroService.getSupherheroConverted(0L)).willReturn(expected);

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

    /*@Test
    @DisplayName("Controller action to get superhero not found returns 404")
    public void testGetSuperheroNotFound() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get(PATH + "/" + 0)
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isNotFound())
                .andReturn();

        ErrorDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                ErrorDto.class);
        assertThat(actual).isInstanceOf(ErrorDto.class);
    }*/

    @Test
    @DisplayName("Controler action to add superhero results created")
    public void testAddSuperhero() throws Exception {
        given(superheroService.addSuperhero(superheroDto)).willReturn(superheroDto);

        MvcResult mvcResult = mockMvc.perform(
                        post(PATH)
                                .content(objectMapper.writeValueAsString(superheroDto))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isCreated())
                .andReturn();

        SuperheroDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SuperheroDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(superheroDto);

        // Verify that the URI in the Location header is correct
        String locationHeader = mvcResult.getResponse().getHeader("Location");
        assertThat(locationHeader).isNotNull();
        assertThat(locationHeader).endsWith("/api/v1/superheroes/" + actual.id());
    }

    @Test
    @DisplayName("Controller action to add superhero returns 422 on missing field in payload")
    @Validated(OnCreate.class)
    public void testAddSuperheroMissingField() throws Exception {
        SuperheroDto failedSuperheroDto = new SuperheroDto(1L, null, null, null, null, null);

        given(superheroService.addSuperhero(failedSuperheroDto)).willReturn(failedSuperheroDto); 
        MvcResult mvcResult = mockMvc.perform(
                        post(PATH)
                                .content(objectMapper.writeValueAsString(failedSuperheroDto))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                )
                        .andDo(print())
                        .andExpect(status().isUnprocessableEntity())
                .andReturn();
        
                ErrorDetailedDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                ErrorDetailedDto.class);
        assertThat(actual).isInstanceOf(ErrorDetailedDto.class);    
    }

    // TODO test roles
}