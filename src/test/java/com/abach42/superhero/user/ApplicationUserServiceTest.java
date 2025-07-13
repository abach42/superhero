package com.abach42.superhero.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tags(value = {@Tag("unit"), @Tag("user")})
@ExtendWith(MockitoExtension.class)
class ApplicationUserServiceTest {

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    private ApplicationUserService subject;

    @BeforeEach
    void setUp() {
        subject = new ApplicationUserService(applicationUserRepository);
    }

    @Test
    @DisplayName("should return user when found by identifier")
    void shouldReturnUserWhenFoundByIdentifier() {
        String email = "test-user";

        ApplicationUser applicationUserStub =
                new ApplicationUser(email, "bar", UserRole.USER);

        given(applicationUserRepository.findOneByEmailAndDeletedIsFalse(email))
                .willReturn(Optional.of(applicationUserStub));

        ApplicationUser result = subject.retrieveUserByEmail(email);

        assertThat(result).isEqualTo(applicationUserStub);
    }

    @Test
    @DisplayName("should throw UserNotFoundException when user not found")
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        String email = "non-existent-user";

        given(applicationUserRepository.findOneByEmailAndDeletedIsFalse(email))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> subject.retrieveUserByEmail(email))
                .isInstanceOf(UserNotFoundException.class);
    }
}