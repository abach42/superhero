package com.abach42.superhero.user;

import com.abach42.superhero.shared.validation.OnCreate;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;

@Schema(name = "user")
public record ApplicationUserDto(
        @Schema(
                title = "login email",
                example = "foo@bar.org",
                description = "Unique email address for login and to send bill."
        )
        @NotNull(groups = OnCreate.class)
        String email,

        @Schema(
                title = "password",
                example = "*****",
                description = "Secret password to login.",
                accessMode = AccessMode.WRITE_ONLY
        )
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotNull(groups = OnCreate.class)
        String password,

        @Schema(
                title = "user role",
                example = "USER",
                description = "Role after login."
        )
        @NotNull(groups = OnCreate.class)
        UserRole role
) {

    public static ApplicationUserDto fromDomain(ApplicationUser applicationUser) {
        return new ApplicationUserDto(
                applicationUser.getEmail(),
                applicationUser.getPassword(),
                applicationUser.getRole());
    }

    public static ApplicationUser toDomain(ApplicationUserDto userDto) {
        return new ApplicationUser(
                userDto.email(),
                userDto.password(),
                userDto.role()
        );
    }
}
