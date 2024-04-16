package com.abach42.superhero.entity.dto;

import java.time.LocalDate;

import com.abach42.superhero.entity.Superhero;

public record SuperheroDto(
    long id,
    String alias,
    String realName, //todo admin only
    LocalDate dateOfBirth,
    //todo make enum type, save byte
    String gender,
    String occupation //todo admin only
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