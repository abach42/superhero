package com.abach42.superhero.entity.dto;

import java.time.LocalDate;

import com.abach42.superhero.entity.Superhero;

import io.swagger.v3.oas.annotations.media.Schema;

public record SuperheroDto(
    @Schema(title = "superhero id", example = "1", description = "Technical id of database record", format = "integer", required = true)
    long id,
    @Schema(title = "alias name", example = "Mr. Incredible", description = "Public alias name of superhero")
    String alias,
    @Schema(title = "real name", example = "Bob Parr", description = "Secret real name of superhero")
    String realName, //todo admin only
    @Schema(title = "birth date", example = "1970-01-01", description = "Birth date of superhero", format = "date")
    LocalDate dateOfBirth,
    //todo make enum type, save byte
    @Schema(title = "gender tag", example = "Male", description = "Gender of superhero")
    String gender,
    @Schema(title = "occupation", example = "Insurance employee", description = "Current apparent profession that the public is supposed to believe is being practiced.")
    String occupation //todo admin only
    //todo add origin story in case of admin role
) {
    public static SuperheroDto fromDomain(Superhero superhero) {
        return new SuperheroDto(
            superhero.getId(), 
            superhero.getAlias(),
            superhero.getAlias(), 
            superhero.getDateOfBirth(), 
            superhero.getGender(),
            superhero.getOccupation()
        );
    }
}