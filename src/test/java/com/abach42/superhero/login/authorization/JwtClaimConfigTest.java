package com.abach42.superhero.login.authorization;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.login.token.AbstractTokenGenerator;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Tags(value = {@Tag("unit"), @Tag("auth")})
class JwtClaimConfigTest {

    private JwtClaimConfig subject;

    @BeforeEach
    void setUp() {
        subject = new JwtClaimConfig();
    }

    @Test
    @DisplayName("should create JWT authentication converter with correct configuration")
    void shouldCreateJwtAuthenticationConverterWithCorrectConfiguration() {
        JwtAuthenticationConverter converter = subject.jwtAuthenticationConverter();

        assertThat(converter).isNotNull();
    }

    @Test
    @DisplayName("should convert authorities claim to granted authorities with ROLE_ prefix")
    void shouldConvertAuthoritiesClaimToGrantedAuthorities() {
        JwtAuthenticationConverter converter = subject.jwtAuthenticationConverter();

        Jwt jwt = createJwtWithAuthorities("USER");

        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);

        assertThat(token.getAuthorities()).hasSize(1);
        assertThat(token.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("should handle admin authorities correctly")
    void shouldHandleMultipleAuthorities() {
        JwtAuthenticationConverter converter = subject.jwtAuthenticationConverter();

        Jwt jwt = createJwtWithAuthorities("ADMIN");

        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);

        assertThat(token.getAuthorities()).hasSize(1);
        assertThat(token.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    private Jwt createJwtWithAuthorities(String authority) {
        return new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of(
                        "sub", "test-user",
                        AbstractTokenGenerator.CLAIM_AUTHORITIES, authority
                )
        );
    }
}