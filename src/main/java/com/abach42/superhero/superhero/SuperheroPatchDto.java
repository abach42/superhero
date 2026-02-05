package com.abach42.superhero.superhero;

import com.abach42.superhero.shared.convertion.PatchField;
import java.time.LocalDate;

public record SuperheroPatchDto(
        PatchField<String> alias,
        PatchField<String> realName,
        PatchField<LocalDate> dateOfBirth,
        PatchField<Gender> gender,
        PatchField<String> occupation,
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
