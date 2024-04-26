package com.abach42.superhero.entity.dto;

import java.time.LocalDate;

import com.abach42.superhero.config.api.OnCreate;
import com.abach42.superhero.config.api.OnUpdate;
import com.abach42.superhero.entity.Superhero;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "superhero")
public record SuperheroDto (
    @Schema(
        title = "superhero id", 
        example = "1", 
        description = "Technical id of database record", 
        format = "integer", 
        accessMode = AccessMode.READ_ONLY
    )
    Long id,

    @Schema(
        title = "alias name", 
        example = "Mr. Incredible", 
        description = "Public alias name of superhero", 
        required = true
    )
    @NotNull(groups = OnCreate.class)
    @Size(max = 20, groups = {OnCreate.class, OnUpdate.class})
    String alias,

    @Schema(
        title = "real name", 
        example = "Bob Parr", 
        description = "Secret real name of superhero", 
        required = true
    )
    @NotNull(groups = OnCreate.class)
    String realName,

    @Schema(
        title = "birth date", 
        example = "1970-01-01", 
        description = "Birth date of superhero", 
        format = "date", 
        required = true
    )
    @NotNull(groups = OnCreate.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth,

    //TODO make enum type, save byte
    @Schema(
        title = "gender tag", 
        example = "Male", 
        description = "Gender of superhero", 
        required = true
    )
    @NotNull(groups = OnCreate.class)
    String gender,

    @Schema(
        title = "occupation", 
        example = "Insurance employee", 
        description = "Current apparent profession that the public is supposed to believe is being practiced."
    )
    String occupation,
    @Schema(
        title = "origin story", 
        example = "Story of superhero."
    )
    String originStory
    ) {
        public static SuperheroDto fromDomain(Superhero superhero) {
            return new SuperheroDto(
                    superhero.getId(),
                    superhero.getAlias(),
                    superhero.getRealName(),
                    superhero.getDateOfBirth(),
                    superhero.getGender(),
                    superhero.getOccupation(),
                    superhero.getOriginStory());
        }

        public static Superhero toDomain(SuperheroDto superheroDto) {
            return new Superhero(
                    superheroDto.alias(),
                    superheroDto.realName(),
                    superheroDto.dateOfBirth(),
                    superheroDto.gender(),
                    superheroDto.occupation(),
                    superheroDto.originStory());
        }
}