package com.abach42.superhero.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags(value = {@Tag("unit"), @Tag("user")})
class ApplicationUserTest {

    private ApplicationUser subject;

    @BeforeEach
    void setUp() {
        subject = new ApplicationUser();
    }

    @Test
    @DisplayName("should get default id value")
    void shouldGetDefaultIdValue() {
        assertThat(subject.getId()).isEqualTo(0L);
    }

    @Test
    @DisplayName("should set and get password")
    void shouldSetAndGetPassword() {
        subject.setPassword("password123");
        assertThat(subject.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("should set and get email")
    void shouldSetAndGetEmail() {
        subject.setEmail("test@example.com");
        assertThat(subject.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("should set and get role")
    void shouldSetAndGetRole() {
        subject.setRole(UserRole.USER);
        assertThat(subject.getRole()).isEqualTo(UserRole.USER);
    }
}
