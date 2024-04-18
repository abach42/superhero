package com.abach42.superhero.functional.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Controller action for superheroes returns first page of superheroes, no page chosen")
    public void testGetAllSuperheros() throws Exception {
        Superhero superhero = new Superhero("foo", "bar", LocalDate.of(1917, 1, 1), "Male", "foo", "foo");
        SuperheroDto superheroDto = SuperheroDto.fromDomain(superhero);
        SuperheroListDto expected = SuperheroListDto.fromPage(new PageImpl<>(List.of(superheroDto),PageRequest.ofSize(1),1L));
       
        given(superheroService.getAllSuperheros(null)).willReturn(expected);

        MvcResult mvcResult = mockMvc.perform(
                get(PATH)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        SuperheroListDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), SuperheroListDto.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Controller action for superhero returns a superhero")
    public void testGetSuperhero() throws Exception {
        long id = 0;
        Superhero superhero = new Superhero("foo", "bar", LocalDate.of(1917, 1, 1), "Male", "foo", "foo");
        SuperheroDto expected = SuperheroDto.fromDomain(superhero);
       
        given(superheroService.getSuperhero(id)).willReturn(Optional.of(expected));

        MvcResult mvcResult = mockMvc.perform(
                get(PATH + "/" + id)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        SuperheroDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), SuperheroDto.class);
        
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Controller action for superhero not found returns 404")
    public void testGetSuperheroNotFound() throws Exception {
        int id = 0;
        mockMvc.perform(
            get(PATH + "/" + id)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(status().reason(SuperheroController.SUPERHERO_NOT_FOUND_MSG + id));
    }

    //todo test roles
}