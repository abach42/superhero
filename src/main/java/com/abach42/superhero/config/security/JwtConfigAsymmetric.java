package com.abach42.superhero.config.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import java.util.List;
import java.util.function.Function;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@ConditionalOnProperty(name = "com.abach42.superhero.security.jwt.symmetric",
        havingValue = "false")
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RsaKeyProperties.class)
public class JwtConfigAsymmetric {

    private final RsaKeyProperties rsaKeys;

    public JwtConfigAsymmetric(RsaKeyProperties rsaKeys) {
        this.rsaKeys = rsaKeys;
    }

    @Bean
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(
                new ImmutableJWKSet<>(
                        new JWKSet(
                                new RSAKey.Builder(rsaKeys.publicKey()).privateKey(
                                        rsaKeys.privateKey()).build())));
    }

    @Bean
    Function<JwtClaimsSet, JwtEncoderParameters> jwtParamBuilder() {
        return JwtEncoderParameters::from;
    }

    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();

        //notice: documentation purpose only
        OAuth2TokenValidator<Jwt> validators = new DelegatingOAuth2TokenValidator<>(
                new JwtClaimValidator<List<String>>("aud", aud -> aud.contains("messaging")),
                new CustomValidator()
        );

        jwtDecoder.setJwtValidator(validators);
        return jwtDecoder;
    }

    static class CustomValidator implements OAuth2TokenValidator<Jwt> {

        OAuth2Error error = new OAuth2Error("12345", "not allowed action", null);

        @Override
        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            if (jwt.getClaim("azp").equals("superhero")) {
                return OAuth2TokenValidatorResult.success();
            } else {
                return OAuth2TokenValidatorResult.failure(error);
            }
        }
    }
}