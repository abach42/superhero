package com.abach42.superhero.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.abach42.superhero.config.PathConfig;
import com.abach42.superhero.entity.dto.SuperheroDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = {ServletWebServerFactoryAutoConfiguration.class},
            	webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class SuperheroControllerValidationTest {
    private final static String PATH = PathConfig.SUPERHEROES;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Controller action to get superheros returns 404")
    public void testGetAllSuperherosReturns404() throws Exception {

       mockMvc.perform(
                        get(PATH).accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Controller action to get superheros returns 422")
    public void testGetAllSuperherosRetunrs422() throws Exception {
        mockMvc.perform(
                get(PATH)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Controller action to get superhero not found returns 404")
    public void testGetSuperheroNotFound() throws Exception {
      
        mockMvc.perform(
                get(PATH + "/" + 0)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    //@Test
    //@DisplayName("Controller action to add superhero returns 422 on missing field in payload")
    public void testAddSuperheroMissingField() throws Exception {
        SuperheroDto failedSuperheroDto = new SuperheroDto(1L, null, null, null, null, null);

        MvcResult mvcResult = mockMvc.perform(
                        post(PATH)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(failedSuperheroDto)))
                        .andDo(print())
                        .andExpect(status().isUnprocessableEntity())
                .andReturn();
        
    }

    
}
