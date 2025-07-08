package com.abach42.superhero.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags(value = {@Tag("unit"), @Tag("user")})
class UserRoleTest {

    @Test
    @DisplayName("should have ADMIN role with value 0")
    void shouldHaveAdminRoleWithValue0() {
        UserRole admin = UserRole.ADMIN;

        assertThat(admin.getValue()).isEqualTo((byte) 0);
    }

    @Test
    @DisplayName("should have USER role with value 1")
    void shouldHaveUserRoleWithValue1() {
        UserRole user = UserRole.USER;

        assertThat(user.getValue()).isEqualTo((byte) 1);
    }

    @Test
    @DisplayName("should return correct enum name for ADMIN")
    void shouldReturnCorrectEnumNameForAdmin() {
        UserRole admin = UserRole.ADMIN;

        assertThat(admin.name()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("should return correct enum name for USER")
    void shouldReturnCorrectEnumNameForUser() {
        UserRole user = UserRole.USER;

        assertThat(user.name()).isEqualTo("USER");
    }

    @Test
    @DisplayName("should convert value 0 to ADMIN using fromValue")
    void shouldConvertValue0ToAdminUsingFromValue() {
        UserRole result = UserRole.fromValue((byte) 0);

        assertThat(result).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("should convert value 1 to USER using fromValue")
    void shouldConvertValue1ToUserUsingFromValue() {
        UserRole result = UserRole.fromValue((byte) 1);

        assertThat(result).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("should throw exception for invalid value in fromValue")
    void shouldThrowExceptionForInvalidValueInFromValue() {
        byte invalidValue = 99;

        assertThatThrownBy(() -> UserRole.fromValue(invalidValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid level value: " + invalidValue);
    }

    @Test
    @DisplayName("should throw exception for negative value in fromValue")
    void shouldThrowExceptionForNegativeValueInFromValue() {
        byte negativeValue = -1;

        assertThatThrownBy(() -> UserRole.fromValue(negativeValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid level value: " + negativeValue);
    }

    @Test
    @DisplayName("should have exactly two enum values")
    void shouldHaveExactlyTwoEnumValues() {
        UserRole[] values = UserRole.values();

        assertThat(values).hasSize(2);
        assertThat(values).containsExactly(UserRole.ADMIN, UserRole.USER);
    }

    @Test
    @DisplayName("should be bidirectional conversion for ADMIN")
    void shouldBeBidirectionalConversionForAdmin() {
        UserRole original = UserRole.ADMIN;
        byte value = original.getValue();
        UserRole converted = UserRole.fromValue(value);

        assertThat(converted).isEqualTo(original);
    }

    @Test
    @DisplayName("should be bidirectional conversion for USER")
    void shouldBeBidirectionalConversionForUser() {
        UserRole original = UserRole.USER;
        byte value = original.getValue();
        UserRole converted = UserRole.fromValue(value);

        assertThat(converted).isEqualTo(original);
    }

    @Test
    @DisplayName("should implement ConvertibleEnum interface")
    void shouldImplementConvertibleEnumInterface() {
        UserRole admin = UserRole.ADMIN;

        assertThat(admin).isInstanceOf(com.abach42.superhero.config.convertion.ConvertibleEnum.class);
    }

    @Test
    @DisplayName("should return Byte wrapper from getValue")
    void shouldReturnByteWrapperFromGetValue() {
        Byte adminValue = UserRole.ADMIN.getValue();
        Byte userValue = UserRole.USER.getValue();

        assertThat(adminValue).isInstanceOf(Byte.class);
        assertThat(userValue).isInstanceOf(Byte.class);
    }

    @Test
    @DisplayName("should maintain enum ordering")
    void shouldMaintainEnumOrdering() {
        UserRole[] values = UserRole.values();

        assertThat(values[0]).isEqualTo(UserRole.ADMIN);
        assertThat(values[1]).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("should have unique values for each enum constant")
    void shouldHaveUniqueValuesForEachEnumConstant() {
        Byte adminValue = UserRole.ADMIN.getValue();
        Byte userValue = UserRole.USER.getValue();

        assertThat(adminValue).isNotEqualTo(userValue);
    }
}
