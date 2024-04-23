package com.abach42.superhero.functional.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.annotation.Validated;

import com.abach42.superhero.config.OnCreate;
import com.abach42.superhero.config.PathConfig;
import com.abach42.superhero.controller.SuperheroController;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.dto.SuperheroDto;
import com.abach42.superhero.entity.dto.SuperheroListDto;
import com.abach42.superhero.service.SuperheroService;
import com.fasterxml.jackson.databind.ObjectMapper;


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
        
        String locationHeader = mvcResult.getResponse().getHeader("Location");
        assertThat(locationHeader).isNotNull();
        assertThat(locationHeader).endsWith("/api/v1/superheroes/" + actual.id());
    }

    /*
     * This test expected to fail runs here, because on WebMvcTest/mockMvc - level jakarta
     * validation constraint is executed, and on usage of validation groups it is executed if annotation is 
     * related directly to method parameter. 
     * 
     */
    @Test
    @DisplayName("Controller action to add superhero returns 422 on missing field in payload")
    @Validated(OnCreate.class)
    public void testAddSuperheroMissingField() throws Exception {
        SuperheroDto failedSuperheroDto = new SuperheroDto(1L, null, null, null, null, null);

        given(superheroService.addSuperhero(failedSuperheroDto)).willReturn(failedSuperheroDto); 
        mockMvc.perform(
                post(PATH)
                        .content(objectMapper.writeValueAsString(failedSuperheroDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Controler action to soft delete superhero result ok")
    public void testSoftDeleteSuperhero() throws Exception {
        given(superheroService.markSuperheroAsDeleted(anyLong())).willReturn(superheroDto);

        MvcResult mvcResult = mockMvc.perform(
                        delete(PATH + "/" + 0)
                                .content(objectMapper.writeValueAsString(superheroDto))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                .andReturn();

        SuperheroDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SuperheroDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(superheroDto);
    }

    // TODO test roles
}