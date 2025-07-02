package com.abach42.superhero.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.util.List;
import org.springframework.data.domain.Page;

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
            title = "pagination",
            description = "Pagination meta data",
            format = "object",
            accessMode = AccessMode.READ_ONLY
    )
    private final PageMeta pageMeta;

    public SuperheroListDto(List<SuperheroDto> superheroes, PageMeta pageMeta) {
        this.superheroes = superheroes;
        this.pageMeta = pageMeta;
    }

    /*
     * instead of page.getTotalElements(), soft delete respected totals by param
     */
    public static SuperheroListDto fromPage(Page<SuperheroDto> page, long totalElements) {
        return new SuperheroListDto(
                page.getContent(),
                new PageMeta(
                        page.getPageable().getPageNumber(),
                        totalPagesRespectSoftDeleted(totalElements, page),
                        page.getPageable().getPageSize(),
                        (int) totalElements));
    }

    private static int totalPagesRespectSoftDeleted(long totalElements, Page<SuperheroDto> page) {
        return (int) Math.ceil((double) totalElements / page.getPageable().getPageSize());
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

    private record PageMeta(
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
                    description = "Number of total superheroes of query",
                    format = "integer",
                    accessMode = AccessMode.READ_ONLY
            )
            Integer totalElements
    ) {

    }
}