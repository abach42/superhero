package com.abach42.superhero.login.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.abach42.superhero.login.token.JwtTokenGenerator;
import com.abach42.superhero.login.token.RefreshTokenGenerator;
import com.abach42.superhero.login.token.TokenResponseDto;
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

@Tags(value = {@Tag("unit"), @Tag("auth")})
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtTokenGenerator jwtTokenGenerator;

    @Mock
    private RefreshTokenGenerator refreshTokenGenerator;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService subject;

    @BeforeEach
    void setUp() {
        subject = new AuthenticationService(jwtTokenGenerator, refreshTokenGenerator);
    }

    @Test
    @DisplayName("should create new token pair with correct values")
    void shouldCreateNewTokenPairWithCorrectValues() {
        String expectedJwtToken = "jwt-token";
        String expectedRefreshToken = "refresh-token";
        int expectedExpirationMinutes = 15;
        int expectedExpirationSeconds = 900;

        given(jwtTokenGenerator.generateToken(authentication)).willReturn(expectedJwtToken);
        given(refreshTokenGenerator.generateToken(authentication)).willReturn(expectedRefreshToken);
        given(jwtTokenGenerator.getExpirationMinutes()).willReturn(expectedExpirationMinutes);

        TokenResponseDto result = subject.createNewTokenPair(authentication);

        assertThat(result.access_token()).isEqualTo(expectedJwtToken);
        assertThat(result.token_type()).isEqualTo(TokenResponseDto.TokenType.BEARER);
        assertThat(result.expires_in()).isEqualTo(expectedExpirationSeconds);
        assertThat(result.refresh_token()).isEqualTo(expectedRefreshToken);
    }

    @Test
    @DisplayName("should convert expiration minutes to seconds")
    void shouldConvertExpirationMinutesToSeconds() {
        int expirationMinutes = 30;
        int expectedExpirationSeconds = 1800;

        given(jwtTokenGenerator.generateToken(authentication)).willReturn("jwt-token");
        given(refreshTokenGenerator.generateToken(authentication)).willReturn("refresh-token");
        given(jwtTokenGenerator.getExpirationMinutes()).willReturn(expirationMinutes);

        TokenResponseDto result = subject.createNewTokenPair(authentication);

        assertThat(result.expires_in()).isEqualTo(expectedExpirationSeconds);
    }
}
