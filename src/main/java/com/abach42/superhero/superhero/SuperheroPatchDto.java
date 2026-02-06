package com.abach42.superhero.superhero;

import com.abach42.superhero.shared.convertion.PatchField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record SuperheroPatchDto(
        @Schema(
                title = "alias name",
                example = "Mr. Incredible",
                description = "Public alias name of superhero",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        PatchField<String> alias,

        @Schema(
                title = "real name",
                example = "Bob Parr",
                description = "Secret real name of superhero",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        PatchField<String> realName,

        @Schema(
                title = "birth date",
                example = "1970-01-01",
                description = "Birth date of superhero",
                format = "date",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        PatchField<LocalDate> dateOfBirth,

        @Schema(
                title = "gender tag",
                example = "MALE",
                description = "Gender of superhero",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        PatchField<Gender> gender,

        @Schema(
                title = "occupation",
                example = "Insurance employee",
                description = "Current apparent profession that the public is supposed to believe is being practiced."
        )
        PatchField<String> occupation,

        @Schema(
                title = "origin story",
                example = "Story of superhero."
        )
        PatchField<String> originStory
) {

    // will be called after deserializing
    public SuperheroPatchDto {
        alias = (alias == null) ? PatchField.missing() : alias;
        realName = (realName == null) ? PatchField.missing() : realName;
        dateOfBirth = (dateOfBirth == null) ? PatchField.missing() : dateOfBirth;
        gender = (gender == null) ? PatchField.missing() : gender;
        occupation = (occupation == null) ? PatchField.missing() : occupation;
        originStory = (originStory == null) ? PatchField.missing() : originStory;
    }
}
