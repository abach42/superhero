package com.abach42.superhero.login.authorization;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@Tags(value = {@Tag("unit"), @Tag("auth")})
@ExtendWith(MockitoExtension.class)
class JwtConfigTest {

    private JwtConfig subject;

    @BeforeEach
    void setUp() {
        subject = new JwtConfig();
        String testTokenSecret = "test-secret-key-for-jwt-that-is-long-enough";
        ReflectionTestUtils.setField(subject, "tokenSecret", testTokenSecret);
    }

    @Test
    @DisplayName("should create JWT encoder with correct configuration")
    void shouldCreateJwtEncoderWithCorrectConfiguration() {
        JwtEncoder encoder = subject.jwtEncoder();

        assertThat(encoder).isInstanceOf(NimbusJwtEncoder.class);
        assertThat(encoder).isNotNull();
    }

    @Test
    @DisplayName("should create JWT decoder with correct configuration")
    void shouldCreateJwtDecoderWithCorrectConfiguration() {
        JwtDecoder decoder = subject.jwtDecoder();

        assertThat(decoder).isInstanceOf(NimbusJwtDecoder.class);
        assertThat(decoder).isNotNull();
    }

    @Test
    @DisplayName("should create JWT parameter builder function")
    void shouldCreateJwtParamBuilderFunction() {
        Function<JwtClaimsSet, JwtEncoderParameters> paramBuilder = subject.jwtParamBuilder();

        assertThat(paramBuilder).isNotNull();
    }

    @Test
    @DisplayName("should build JWT encoder parameters with correct algorithm")
    void shouldBuildJwtEncoderParametersWithCorrectAlgorithm() {
        Function<JwtClaimsSet, JwtEncoderParameters> paramBuilder = subject.jwtParamBuilder();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject("test-user")
                .issuedAt(Instant.now())
                .build();

        JwtEncoderParameters parameters = paramBuilder.apply(claims);

        assertThat(parameters).isNotNull();
        assertThat(parameters.getJwsHeader().getAlgorithm()).isEqualTo(MacAlgorithm.HS256);
    }

    @Test
    @DisplayName("should use HS256 as MAC algorithm")
    void shouldUseHS256AsMacAlgorithm() {
        assertThat(JwtConfig.MAC_ALGORITHM).isEqualTo(MacAlgorithm.HS256);
    }
}
