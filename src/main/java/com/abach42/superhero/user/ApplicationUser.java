package com.abach42.superhero.user;

import com.abach42.superhero.config.validation.OnCreate;
import com.abach42.superhero.config.validation.OnUpdate;
import com.abach42.superhero.superhero.Superhero;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.hibernate.validator.constraints.Length;

/**
 * Part of aggregate root {@link Superhero} 1:1, representing `User` of spring security.
 * {@link com.abach42.superhero.login.authorization.UserDetailsServiceConfig} Password hidden when
 * display in response body.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "application_user")
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Schema(
            title = "login email",
            example = "foo@bar.org",
            description = "Unique email address for login and to send bill.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Column(length = 250, unique = true)
    @NotNull(groups = OnCreate.class)
    private String email;

    @Schema(
            title = "password",
            example = "*****",
            description = "Secret password to login.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Length(groups = OnCreate.class, min = 2, max = 255, message =
            "Password must be between 2 and 255 characters.")
    @Null(groups = OnUpdate.class)
    private String password;

    @Schema(
            title = "user role",
            example = "USER",
            description = "Role after login.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(groups = OnCreate.class)
    @Convert(converter = UserRoleConverter.class)
    private UserRole role;

    private Boolean deleted = false;

    protected ApplicationUser() {
    }

    public ApplicationUser(String email, String password, UserRole role) {
        setEmail(email);
        setPassword(password);
        setRole(role);
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
