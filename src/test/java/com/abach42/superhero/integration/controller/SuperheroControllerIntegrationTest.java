package com.abach42.superhero.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.abach42.superhero.config.PathConfig;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.dto.ErrorDto;
import com.abach42.superhero.integration.configuration.TestContainerConfiguration;
import com.abach42.superhero.repository.SuperheroRepository;
import com.abach42.superhero.service.SuperheroService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/*
 * End to end test with database and mock client
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SuperheroControllerIntegrationTest {
    private final static String PATH = PathConfig.SUPERHEROES;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final int TOTAL_PAGES = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SuperheroRepository superheroRepository;

    @Test
    @DisplayName("(mockmvc + db) get all supeheroes not found")
    public void testGetAllSuperherosFailsWhenNoSuperhero() throws Exception {
        superheroRepository.deleteAll();

        MvcResult mvcResult = mockMvc.perform(
                    get(PATH).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
            .andReturn();
       
            
        ErrorDto actual = getErrorDtoFromResutPayload(mvcResult);
        assertThat(actual).usingRecursiveComparison().isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(), 
                HttpStatus.NOT_FOUND.getReasonPhrase(), SuperheroService.SUPERHEROES_NOT_FOUND_MSG, PATH));
    }

    private ErrorDto getErrorDtoFromResutPayload(MvcResult mvcResult)
            throws JsonProcessingException, JsonMappingException, UnsupportedEncodingException {
        ErrorDto actual = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), ErrorDto.class);
        return actual;
    }
    
    @Test
    @DisplayName("(mockmvc + db) get all superheros page number too high" )
    public void testGetAllSuperherosFailsWhenPageNotExists() throws Exception {
        int failPage = 999; //assuming test data have max 998 pages

        MvcResult mvcResult = mockMvc.perform(
                    get(PATH + "?page="+ failPage).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
            .andReturn();

        ErrorDto actual = getErrorDtoFromResutPayload(mvcResult);
        assertThat(actual).usingRecursiveComparison().isEqualTo(new ErrorDto(HttpStatus.UNPROCESSABLE_ENTITY.value(), 
                    HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(), 
                    SuperheroService.MAX_PAGE_EXEEDED_MSG + " Total: " + TOTAL_PAGES + ", requested: " + failPage + ".",
                    PATH));
    }

    @Test
    @DisplayName("(mockmvc + db) get a superhero not found")
    public void testGetSuperheroFailsWhenNoSuperhero() throws Exception {
        superheroRepository.deleteAll();

        MvcResult mvcResult = mockMvc.perform(
                    get(PATH + "/" + 1).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
            .andReturn();
       
            
        ErrorDto actual = getErrorDtoFromResutPayload(mvcResult);
        assertThat(actual).usingRecursiveComparison().isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(), 
                HttpStatus.NOT_FOUND.getReasonPhrase(), SuperheroService.SUPERHERO_NOT_FOUND_MSG + 1, PATH + "/" + 1));
    }

    @Test
    @DisplayName("(mockmvc + db) add new superhero fails when missing field")
    public void testAddSuperheroFailsWhenMissingFieldInPayload() throws Exception {
        Superhero failingSuperhero = new Superhero(null, "bar", LocalDate.of(1917, 1, 1), "Male", "foo", "foo");
        mockMvc.perform(
                post(PATH)
                    .content(OBJECT_MAPPER.writeValueAsString(failingSuperhero))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("(mockmvc + db) add new superhero fails when missing")
    public void testAddSuperheroFailsWhenEmptyPayload() throws Exception {
        mockMvc.perform(
                post(PATH)
                    .content(OBJECT_MAPPER.writeValueAsString(null))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("(mockmvc + db) update superhero not found")
    public void testUpdateSuperheroFailsWhenNoSuperheroFound() throws Exception {
        superheroRepository.deleteAll();

        Superhero superhero = new Superhero("foo", "bar", LocalDate.of(1917, 1, 1), "Male", "baz", "boo");

        MvcResult mvcResult = mockMvc.perform(
                    put(PATH + "/" + 1)
                        .content(OBJECT_MAPPER.writeValueAsString(superhero))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
            .andReturn();
       
            
        ErrorDto actual = getErrorDtoFromResutPayload(mvcResult);
        assertThat(actual).usingRecursiveComparison().isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(), 
                HttpStatus.NOT_FOUND.getReasonPhrase(), SuperheroService.SUPERHERO_NOT_FOUND_MSG + 1, 
                PATH + "/" + 1));
    }

    @Test
    @DisplayName("(mockmvc + db) update superhero fails when missing field")
    public void testUpdateSuperheroFailsWhenMissingFieldInPayload() throws Exception {
        mockMvc.perform(
                put(PATH + "/" + 1)
                    .content(OBJECT_MAPPER.writeValueAsString(null))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("(mockmvc + db) soft delete superhero not found")
    public void testSoftDeleteSuperheroFailsWhenNotFound() throws Exception {
        superheroRepository.deleteAll();

        MvcResult mvcResult = mockMvc.perform(
                    delete(PATH + "/" + 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
            .andReturn();

            
        ErrorDto actual = getErrorDtoFromResutPayload(mvcResult);
        assertThat(actual).usingRecursiveComparison().isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(), 
                HttpStatus.NOT_FOUND.getReasonPhrase(), SuperheroService.SUPERHERO_NOT_FOUND_MSG + 1, 
                PATH + "/" + 1));
    }
}