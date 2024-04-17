package com.abach42.superhero.functional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.abach42.superhero.controller.SuperheroController;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.dto.SuperheroDto;
import com.abach42.superhero.repository.SuperheroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

@WebMvcTest(SuperheroController.class)
public class SuperheroControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SuperheroRepository superheroRepository;

    @Test
    public void testGetAllSuperheros() throws Exception {
        Superhero superhero = new Superhero("foo", "bar", LocalDate.of(1917, 1, 1), "Male", "foo", "foo");
        SuperheroDto expected = SuperheroDto.fromDomain(superhero);
       
        when(superheroRepository.findAll()).thenReturn(List.of(superhero));

        MvcResult mvcResult = mockMvc.perform(
                get("/api/superheros/")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        ObjectReader reader = objectMapper.readerForListOf(SuperheroDto.class);
        List<SuperheroDto> actual = reader.readValue(mvcResult.getResponse().getContentAsString());

        assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(expected);
    }

    //todo test roles
    //todo  .andExpect(status().isBadRequest()).andExpect(responseBody().containsError("name", "must not be null")); 
}