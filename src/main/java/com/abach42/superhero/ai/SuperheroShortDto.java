package com.abach42.superhero.ai;

import com.abach42.superhero.superhero.Gender;
import com.abach42.superhero.superhero.Superhero;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;

@Schema(name = "superhero without user information")
public record SuperheroShortDto(
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
        String alias,

        @Schema(
                title = "real name",
                example = "Bob Parr",
                description = "Secret real name of superhero",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String realName,

        @Schema(
                title = "birth date",
                example = "1970-01-01",
                description = "Birth date of superhero",
                format = "date",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate dateOfBirth,

        @Schema(
                title = "gender tag",
                example = "FEMALE",
                description = "Gender of superhero",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
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
        String originStory
) {

    public static SuperheroShortDto fromDomain(Superhero superhero) {
        return new SuperheroShortDto(
                superhero.getId(),
                superhero.getAlias(),
                superhero.getRealName(),
                superhero.getDateOfBirth(),
                superhero.getGender(),
                superhero.getOccupation(),
                superhero.getOriginStory());
    }
}