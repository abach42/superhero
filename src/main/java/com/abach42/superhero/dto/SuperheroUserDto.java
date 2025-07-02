package com.abach42.superhero.dto;

import com.abach42.superhero.config.api.OnCreate;
import com.abach42.superhero.entity.SuperheroUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;

@Schema(name = "user")
public record SuperheroUserDto(
        @Schema(
                title = "login email",
                example = "foo@bar.org",
                description = "Unique email address for login and to send bill.",
                required = true
        )
        @NotNull(groups = OnCreate.class)
        String email,

        @Schema(
                title = "password",
                example = "*****",
                description = "Secret password to login.",
                required = true,
                accessMode = AccessMode.WRITE_ONLY
        )
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotNull(groups = OnCreate.class)
        String password,

        @Schema(
                title = "user role",
                example = "USER",
                description = "Role after login.",
                required = true
        )
        @NotNull(groups = OnCreate.class)
        String role
) {

    public static SuperheroUserDto fromDomain(SuperheroUser superheroUser) {
        return new SuperheroUserDto(
                superheroUser.getEmail(),
                superheroUser.getPassword(),
                RoleEnum.getFromString(superheroUser.getRole()));
    }

    public static SuperheroUser toDomain(SuperheroUserDto userDto) {
        return new SuperheroUser(
                userDto.email(),
                userDto.password(),
                userDto.role()
        );
    }

    public enum RoleEnum {
        ADMIN("ADMIN"),
        USER("USER");

        private final String role;

        RoleEnum(String role) {
            this.role = role;
        }

        public static String getFromString(String role) {
            for (RoleEnum userRole : RoleEnum.values()) {
                if (userRole.name().equalsIgnoreCase(role)) {
                    return userRole.getRole();
                }
            }
            throw new IllegalArgumentException("No matching role for " + role);
        }

        public String getRole() {
            return role;
        }
    }
}
