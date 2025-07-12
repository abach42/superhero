package com.abach42.superhero.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.testconfiguration.TestContainerConfiguration;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@Tags(value = {@Tag("integration"), @Tag("user")})
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ApplicationUserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicationUserRepository subject;

    private ApplicationUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new ApplicationUser();
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.USER);
    }

    @Test
    @DisplayName("should save and find user by email")
    void shouldSaveAndFindUserByEmail() {
        entityManager.persistAndFlush(testUser);

        Optional<ApplicationUser> result =
                subject.findOneByEmailAndDeletedIsFalse("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("should work with USER role security context")
    void shouldWorkWithUserRoleSecurityContext() {
        entityManager.persistAndFlush(testUser);

        Optional<ApplicationUser> result =
                subject.findOneByEmailAndDeletedIsFalse("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("should work with ADMIN role security context")
    void shouldWorkWithAdminRoleSecurityContext() {
        testUser.setEmail("admin666@example.com");
        testUser.setRole(UserRole.ADMIN);
        entityManager.persistAndFlush(testUser);

        Optional<ApplicationUser> result =
                subject.findOneByEmailAndDeletedIsFalse("admin666@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo(UserRole.ADMIN);
    }
}
