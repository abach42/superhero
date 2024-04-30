package com.abach42.superhero.entity;

import com.abach42.superhero.config.api.OnCreate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Access(AccessType.FIELD)
@Table(name = "superhero_user")
public class SuperheroUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Schema(
        title = "login email", 
        example = "foo@bar.org", 
        description = "Unique email address for login and to send bill.", 
        required = true
    )
    @NotNull(groups = OnCreate.class)
    private String email;
    
    @Schema(
        title = "password", 
        example = "*****", 
        description = "Secret password to login.", 
        required = true
    )
    @NotNull(groups = OnCreate.class)
    private String password;

    @Schema(
        title = "user role", 
        example = "USER", 
        description = "Role after login.", 
        required = true
    )
    @NotNull(groups = OnCreate.class)
    private String role;
    
    private Boolean deleted = false;

    protected SuperheroUser() {
    }

    public SuperheroUser(String email, String password, String role) {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
