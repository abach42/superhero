package com.abach42.superhero.entity.dto;

import java.util.List;

import org.springframework.data.domain.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@Schema(name = "superheroes")
public class SuperheroListDto {
    @Schema(
        title = "superheroes", 
        description = "List of superheroes", 
        format = "array", 
        accessMode = AccessMode.READ_ONLY
    )
    private final List<SuperheroDto> superheroes;

    @Schema(
        title = "pagnation", 
        description = "Pagination meta data", 
        format = "object", 
        accessMode = AccessMode.READ_ONLY
    )
    private final PageMeta pageMeta;

    public SuperheroListDto(List<SuperheroDto> superheroes, PageMeta pageMeta) {
        this.superheroes = superheroes;
        this.pageMeta = pageMeta;
    }

    public List<SuperheroDto> getSuperheroes() {
        return superheroes;
    }

    public PageMeta getPageMeta() {
        return pageMeta;
    }

    @Hidden
    @JsonIgnore
    public Boolean isEmpty() {
        return superheroes.isEmpty();
    }

    public static SuperheroListDto fromPage(Page<SuperheroDto> page) {
        return new SuperheroListDto(
                page.getContent(),
                new PageMeta(
                        page.getPageable().getPageNumber(),
                        page.getTotalPages(),
                        page.getPageable().getPageSize(),
                        page.getTotalElements()));
    }
    
    private static record PageMeta (
        @Schema(
            title = "page number", 
            example = "0",
            description = "Actual page number, first page is 0", 
            format = "integer", 
            accessMode = AccessMode.READ_ONLY
        )
        Integer pageNumber, 
        @Schema(
            title = "total pages", 
            description = "Number of total pages of actual query", 
            format = "integer", 
            accessMode = AccessMode.READ_ONLY
        )
        Integer totalPages, 
        @Schema(
            title = "page size", 
            description = "Number of superheroes per size", 
            format = "integer", 
            accessMode = AccessMode.READ_ONLY
        )
        Integer pageSize, 
        @Schema(
            title = "total elements", 
            description = "Number of total supherheroes of query", 
            format = "integer", 
            accessMode = AccessMode.READ_ONLY
        )
        Long totalElements
    ) {}
}