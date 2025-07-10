package com.abach42.superhero.superhero;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.testconfiguration.TestStubs;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@Tags(value = {@Tag("unit"), @Tag("superhero")})
public class SuperheroListDtoTest {

    @Test
    @DisplayName("Superhero list test with pages")
    public void testFromPage() {
        SuperheroDto superhero1 = SuperheroDto.fromDomain(TestStubs.getSuperheroStub());
        SuperheroDto superhero2 = SuperheroDto.fromDomain(TestStubs.getSuperheroStub());

        List<SuperheroDto> superheroDtos = List.of(superhero1, superhero2);

        Page<SuperheroDto> page = new PageImpl<>(superheroDtos, PageRequest.of(0, 10), 20);

        SuperheroListDto actual = SuperheroListDto.fromPage(page, 20);

        assertThat(String.valueOf(actual.pageMeta())).isEqualTo(
                "PageMeta[pageNumber=0, totalPages=2, pageSize=10, totalElements=20]");
    }
}