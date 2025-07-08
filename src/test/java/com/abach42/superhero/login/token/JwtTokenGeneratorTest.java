package com.abach42.superhero.login.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.abach42.superhero.user.ApplicationUser;
import com.abach42.superhero.user.ApplicationUserService;
import com.abach42.superhero.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;

@Tags(value = {@Tag("unit"), @Tag("auth")})
@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Token Generator")
class JwtTokenGeneratorTest {

    @Mock
    private ApplicationUserService applicationUserService;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private ApplicationUser applicationUser;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private JwtTokenGenerator subject;

    @BeforeEach
    void setUp() {
        subject = new JwtTokenGenerator(applicationUserService, jwtEncoder);
    }

    @Test
    @DisplayName("should return AUTH as allowed action")
    void shouldReturnAuthAsAllowedAction() {
        TokenPurpose allowedAction = subject.getAllowedAction();

        assertThat(allowedAction).isEqualTo(TokenPurpose.AUTH);
    }

    @Test
    @DisplayName("should return 15 minutes as expiration time")
    void shouldReturn15MinutesAsExpirationTime() {
        int expirationMinutes = subject.getExpirationMinutes();

        assertThat(expirationMinutes).isEqualTo(15);
    }

    @Test
    @DisplayName("should generate token with correct claims")
    void shouldGenerateTokenWithCorrectClaims() {
        String email = "test-user";
        String tokenValue = "generated-token";

        given(authentication.getName()).willReturn(email);
        given(applicationUserService.retrieveUserByEmail(email))
                .willReturn(applicationUser);
        given(applicationUser.getRole()).willReturn(UserRole.USER);
        given(jwtEncoder.encode(any())).willReturn(jwt);
        given(jwt.getTokenValue()).willReturn(tokenValue);

        String result = subject.generateToken(authentication);

        assertThat(result).isEqualTo(tokenValue);
    }
}
