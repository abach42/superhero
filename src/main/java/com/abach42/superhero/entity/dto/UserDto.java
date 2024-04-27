package com.abach42.superhero.entity.dto;

import com.abach42.superhero.config.api.OnCreate;
import com.abach42.superhero.entity.SuperheroUser;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "user")
public record UserDto(
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
        required = true
    )
    @NotNull(groups = OnCreate.class)
    String password,

    @Schema(
        title = "user role", 
        example = "USER", 
        description = "Role after login.", 
        required = true
    )
    @NotNull(groups = OnCreate.class)
    String[] role
) {
    public static UserDto fromDomain(SuperheroUser superheroUser) {
        return new UserDto(
                superheroUser.getEmail(),
                superheroUser.getPassword(),
                RoleEnum.getFromString(superheroUser.getRole()));
    }

    //TODO: test admin, user unit
    public static enum RoleEnum {
        ADMIN(new String[]{"ADMIN", "USER"}),
        USER(new String[]{"USER"});

        private final String[] roles;

        RoleEnum(String[] roles) {
            this.roles = roles;
        }

        public String[] getRoles() {
            return roles;
        }

        public static String[] getFromString(String role) {
            for (RoleEnum userRole : RoleEnum.values()) {
                if (userRole.name().equalsIgnoreCase(role)) {
                    return userRole.getRoles();
                }
            }
            throw new IllegalArgumentException("No matching role for " + role);
        }
    }
}
