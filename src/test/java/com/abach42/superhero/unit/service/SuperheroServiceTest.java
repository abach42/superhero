package com.abach42.superhero.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.dto.SuperheroListDto;
import com.abach42.superhero.exception.ApiException;
import com.abach42.superhero.repository.SuperheroRepository;
import com.abach42.superhero.service.SuperheroService;

@ExtendWith(MockitoExtension.class)
public class SuperheroServiceTest {
    @Mock
    private SuperheroRepository superheroRepository;

    private SuperheroService superheroService;

    @Test
    @DisplayName("Get all heroes, get all heroes by null page")
    public void testGetAllHerores() {
        superheroService = new SuperheroService(superheroRepository, 10);

        Superhero superhero = new Superhero("foo", "bar", LocalDate.of(1917, 1, 1), "Male", "foo", "foo");
        Page<Superhero> page = new PageImpl<>(List.of(superhero),PageRequest.ofSize(1),1L);
        
        given(superheroRepository.findAll(any(PageRequest.class))).willReturn(page);

        SuperheroListDto result = superheroService.getAllSuperheros(1);

        assertNotNull(result);
        verify(superheroRepository).findAll(PageRequest.of(1, 10));
    }

    @Test
    @DisplayName("Get all heroes, throws unprocessable, beause of pageNumber is higher than total pages")
    void testGetAllHeroesThrowsUnprocessableEntity() {
        superheroService = new SuperheroService(superheroRepository, 10);

        Page<Superhero> page = new PageImpl<>(List.of(), PageRequest.ofSize(1), 0L);

        given(superheroRepository.findAll(any(PageRequest.class))).willReturn(page);

        ApiException exception = assertThrows(ApiException.class, 
            () -> superheroService.getAllSuperheros(2));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(exception.getMessage()).containsSequence("The total page number has been exceeded.");
    }

    @Test
    @DisplayName("Get all heroes, throws notfound, because superheroPage is empty")
    void testGetAllHeroesThrowsNotFound() {
        superheroService = new SuperheroService(superheroRepository, 10);
    
        Page<Superhero> page = new PageImpl<>(List.of(), PageRequest.ofSize(1), 0L);
    
        given(superheroRepository.findAll(any(PageRequest.class))).willReturn(page);
    
        ApiException exception = assertThrows(ApiException.class, 
            () -> superheroService.getAllSuperheros(null));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).containsSequence("Superheroes not found");
    }
}