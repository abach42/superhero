package com.abach42.superhero.superhero;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags(value = {@Tag("unit"), @Tag("superhero")})
class GenderConverterTest {

    private final GenderConverter subject = new GenderConverter();

    @Test
    @DisplayName("should convert enum to database and back")
    void shouldConvertEnumToDatabaseAndBack() {
        assertThat(subject.convertToDatabaseColumn(Gender.MALE)).isEqualTo((byte) 0);
        assertThat(subject.convertToDatabaseColumn(Gender.FEMALE)).isEqualTo((byte) 1);
        assertThat(subject.convertToDatabaseColumn(Gender.HIDDEN)).isEqualTo((byte) 2);
        assertThat(subject.convertToDatabaseColumn(null)).isNull();

        assertThat(subject.convertToEntityAttribute((byte) 0)).isEqualTo(Gender.MALE);
        assertThat(subject.convertToEntityAttribute((byte) 1)).isEqualTo(Gender.FEMALE);
        assertThat(subject.convertToEntityAttribute((byte) 2)).isEqualTo(Gender.HIDDEN);
        assertThat(subject.convertToEntityAttribute(null)).isNull();
    }

    @Test
    @DisplayName("should throw exception for invalid byte value")
    void shouldThrowExceptionForInvalidByteValue() {
        assertThatThrownBy(() -> subject.convertToEntityAttribute((byte) 99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Gender value: 99");
    }
}