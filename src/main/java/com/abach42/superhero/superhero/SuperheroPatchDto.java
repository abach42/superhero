package com.abach42.superhero.superhero;

import java.time.LocalDate;
import java.util.Optional;

public record SuperheroPatchDto(
        Optional<String> alias,
        Optional<String> realName,
        Optional<LocalDate> dateOfBirth,
        Optional<Gender> gender,
        Optional<String> occupation,
        Optional<String> originStory
) {
    public static SuperheroPatchDto create() {
        return new SuperheroPatchDto(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }
}
