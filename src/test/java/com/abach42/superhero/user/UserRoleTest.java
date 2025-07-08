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
    @DisplayName("should have exactly two enum values")
    void shouldHaveExactlyTwoEnumValues() {
        UserRole[] values = UserRole.values();

        assertThat(values).hasSize(2);
        assertThat(values).containsExactly(UserRole.ADMIN, UserRole.USER);
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
