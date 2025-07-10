package com.abach42.superhero.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags(value = {@Tag("unit"), @Tag("user")})
class ApplicationUserDtoTest {

    @Test
    @DisplayName("should create ApplicationUserDto with all fields")
    void shouldCreateApplicationUserDtoWithAllFields() {
        ApplicationUserDto dto = new ApplicationUserDto(
                "test@example.com",
                "password123",
                UserRole.USER
        );

        assertThat(dto.email()).isEqualTo("test@example.com");
        assertThat(dto.password()).isEqualTo("password123");
        assertThat(dto.role()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("should convert from ApplicationUser domain object")
    void shouldConvertFromApplicationUserDomainObject() {
        ApplicationUser user = new ApplicationUser(
                "user@test.com",
                "secret",
                UserRole.ADMIN
        );

        ApplicationUserDto dto = ApplicationUserDto.fromDomain(user);

        assertThat(dto.email()).isEqualTo("user@test.com");
        assertThat(dto.password()).isEqualTo("secret");
        assertThat(dto.role()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("should convert to ApplicationUser domain object")
    void shouldConvertToApplicationUserDomainObject() {
        ApplicationUserDto dto = new ApplicationUserDto(
                "admin@test.com",
                "adminpass",
                UserRole.ADMIN
        );

        ApplicationUser user = ApplicationUserDto.toDomain(dto);

        assertThat(user.getEmail()).isEqualTo("admin@test.com");
        assertThat(user.getPassword()).isEqualTo("adminpass");
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("should handle USER role conversion")
    void shouldHandleUserRoleConversion() {
        ApplicationUserDto dto = new ApplicationUserDto(
                "user@example.com",
                "userpass",
                UserRole.USER
        );

        ApplicationUser user = ApplicationUserDto.toDomain(dto);

        assertThat(user.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("should handle round-trip conversion")
    void shouldHandleRoundTripConversion() {
        ApplicationUser originalUser = new ApplicationUser(
                "roundtrip@test.com",
                "roundtrippass",
                UserRole.USER
        );

        ApplicationUserDto dto = ApplicationUserDto.fromDomain(originalUser);
        ApplicationUser convertedUser = ApplicationUserDto.toDomain(dto);

        assertThat(convertedUser.getEmail()).isEqualTo(originalUser.getEmail());
        assertThat(convertedUser.getPassword()).isEqualTo(originalUser.getPassword());
        assertThat(convertedUser.getRole()).isEqualTo(originalUser.getRole());
    }
}
