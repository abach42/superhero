package com.abach42.superhero.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags(value = {@Tag("unit"), @Tag("user")})
class UserRoleConverterTest {

    private final UserRoleConverter subject = new UserRoleConverter();

    @Test
    @DisplayName("should convert enum to database and back")
    void shouldConvertEnumToDatabaseAndBack() {
        assertThat(subject.convertToDatabaseColumn(UserRole.ADMIN)).isEqualTo((byte) 0);
        assertThat(subject.convertToDatabaseColumn(UserRole.USER)).isEqualTo((byte) 1);
        assertThat(subject.convertToDatabaseColumn(null)).isNull();

        assertThat(subject.convertToEntityAttribute((byte) 0)).isEqualTo(UserRole.ADMIN);
        assertThat(subject.convertToEntityAttribute((byte) 1)).isEqualTo(UserRole.USER);
        assertThat(subject.convertToEntityAttribute(null)).isNull();
    }

    @Test
    @DisplayName("should throw exception for invalid byte value")
    void shouldThrowExceptionForInvalidByteValue() {
        assertThatThrownBy(() -> subject.convertToEntityAttribute((byte) 99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid UserRole value: 99");
    }
}