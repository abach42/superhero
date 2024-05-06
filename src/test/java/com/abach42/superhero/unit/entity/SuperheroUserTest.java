package com.abach42.superhero.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.abach42.superhero.entity.SuperheroUser;

public class SuperheroUserTest {
    private SuperheroUser superheroUser;

    @BeforeEach
    public void setUp() {
        superheroUser = new SuperheroUser("foo", "bar", "baz");
    }

    @Test
    @DisplayName("SuperheroUser entity getters get and setters set.")
    public void testGettersAndSetters() {
        assertThat("foo").isEqualTo(superheroUser.getEmail());
        assertThat("bar").isEqualTo(superheroUser.getPassword());
        assertThat("baz").isEqualTo(superheroUser.getRole());
        assertThat(false).isEqualTo(superheroUser.getDeleted());

        superheroUser.setEmail("test@example.com");
        superheroUser.setPassword("password");
        superheroUser.setRole("USER");
        superheroUser.setDeleted(true);

        assertThat("test@example.com").isEqualTo(superheroUser.getEmail());
        assertThat("password").isEqualTo(superheroUser.getPassword());
        assertThat("USER").isEqualTo(superheroUser.getRole());
        assertThat(true).isEqualTo(superheroUser.getDeleted());
    }
}