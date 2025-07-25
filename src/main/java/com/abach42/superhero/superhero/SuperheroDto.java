package com.abach42.superhero.superhero;

import com.abach42.superhero.config.validation.OnCreate;
import com.abach42.superhero.config.validation.OnUpdate;
import com.abach42.superhero.user.ApplicationUserDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(name = "superhero")
public record SuperheroDto(
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
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(groups = OnCreate.class)
        @Size(max = 20, groups = {OnCreate.class, OnUpdate.class})
        String alias,

        @Schema(
                title = "real name",
                example = "Bob Parr",
                description = "Secret real name of superhero",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(groups = OnCreate.class)
        String realName,

        @Schema(
                title = "birth date",
                example = "1970-01-01",
                description = "Birth date of superhero",
                format = "date",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(groups = OnCreate.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate dateOfBirth,

        @Schema(
                title = "gender tag",
                example = "FEMALE",
                description = "Gender of superhero",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(groups = OnCreate.class)
        Gender gender,

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
        String originStory,

        @NotNull(groups = OnCreate.class)
        @Valid
        ApplicationUserDto user
) {

    public static SuperheroDto fromDomain(Superhero superhero) {
        return new SuperheroDto(
                superhero.getId(),
                superhero.getAlias(),
                superhero.getRealName(),
                superhero.getDateOfBirth(),
                superhero.getGender(),
                superhero.getOccupation(),
                superhero.getOriginStory(),
                ApplicationUserDto.fromDomain(superhero.getUser()));
    }

    public static Superhero toDomain(SuperheroDto superheroDto) {
        return new Superhero(
                superheroDto.alias(),
                superheroDto.realName(),
                superheroDto.dateOfBirth(),
                superheroDto.gender(),
                superheroDto.occupation(),
                superheroDto.originStory(),
                ApplicationUserDto.toDomain(superheroDto.user()));
    }
}