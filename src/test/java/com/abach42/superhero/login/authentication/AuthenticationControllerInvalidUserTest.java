package com.abach42.superhero.login.authentication;

import com.abach42.superhero.testconfiguration.TestContainerConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureMockMvc
@Testcontainers
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthenticationControllerInvalidUserTest {

    @Test
    @DisplayName("Spring context fails to load if user is not found in db")
    void testContextFailsWithNonExistentUser() {
        new ApplicationContextRunner()
                .withUserConfiguration(InvalidUserContext.class)
                .run(context -> {

                });
    }

    @Configuration
    @SpringBootTest(classes = TestContainerConfiguration.class)
    @AutoConfigureMockMvc
    @WithUserDetails("nonexistent_user")
    static class InvalidUserContext {

    }
}

