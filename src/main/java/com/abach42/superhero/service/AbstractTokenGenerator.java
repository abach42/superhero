package com.abach42.superhero.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractTokenGenerator {
    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private Function<JwtClaimsSet, JwtEncoderParameters> jwtParamBuilder;

    abstract int getExpirationMinutes();
    abstract String getScope();
    
    public String generate(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(getExpirationMinutes(), ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim("scope", scope + " " + getScope())
                .claim("azp", "superhero")
                .claim("aud", "messaging")
                .build();
        
        return this.jwtEncoder.encode(jwtParamBuilder.apply(claims)).getTokenValue();
    }
} 