package com.abach42.superhero.unit.entity.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.abach42.superhero.entity.dto.SuperheroDto;
import com.abach42.superhero.entity.dto.SuperheroListDto;

public class SuperheroListDtoTest {

    @Test
    public void testFromPage() {
        SuperheroDto superhero1 = new SuperheroDto(1L, "Mr. Incredible", "Bob Parr", LocalDate.of(1970, 1, 1), "Male", "Insurance employee");
        SuperheroDto superhero2 = new SuperheroDto(2L, "Batman", "Bruce Wayne", LocalDate.of(1939, 5, 1), "Male", "Businessman");
        
        List<SuperheroDto> superheroDtos = List.of(superhero1, superhero2);
        
        Page<SuperheroDto> page = new PageImpl<>(superheroDtos, PageRequest.of(0, 10), 20);
        
        SuperheroListDto actual = SuperheroListDto.fromPage(page, 20);
        
        assertThat(String.valueOf(actual.getPageMeta())).isEqualTo("PageMeta[pageNumber=0, totalPages=2, pageSize=10, totalElements=20]");
    }
}