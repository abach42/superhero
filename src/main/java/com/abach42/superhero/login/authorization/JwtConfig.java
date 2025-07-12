package com.abach42.superhero.login.authorization;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.util.function.Function;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration(proxyBeanMethods = false)
public class JwtConfig {

    public static final MacAlgorithm MAC_ALGORITHM = MacAlgorithm.HS256;

    @Value("${com.abach42.superhero.security.jwt.token-secret}")
    private String tokenSecret;

    @Bean
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(tokenSecret.getBytes()));
    }

    @Bean
    Function<JwtClaimsSet, JwtEncoderParameters> jwtParamBuilder() {
        return claims -> JwtEncoderParameters.from(
                JwsHeader.with(MAC_ALGORITHM).build(), claims);
    }

    @Bean
    JwtDecoder jwtDecoder() {
        byte[] bytes = tokenSecret.getBytes();
        SecretKeySpec originalKey = new SecretKeySpec(bytes, 0, bytes.length, "RSA");

        return NimbusJwtDecoder.withSecretKey(originalKey).macAlgorithm(MAC_ALGORITHM).build();
    }
}