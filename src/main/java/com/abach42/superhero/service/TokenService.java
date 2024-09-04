package com.abach42.superhero.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.abach42.superhero.dto.TokenDto;

@Service
public class TokenService {
    AbstractTokenGenerator jwTokenGenerator;
    AbstractTokenGenerator refresTokenGenerator;

    TokenService(AbstractTokenGenerator jwTokenGenerator, 
        AbstractTokenGenerator refresTokenGenerator) {
        this.jwTokenGenerator = jwTokenGenerator;
        this.refresTokenGenerator = refresTokenGenerator;
    }

    public TokenDto generateTokenPair(Authentication authentication) {
        String jwt = retrieveJwt(authentication);
        String refreshToken = retrieveRefreshToken(authentication);

        return new TokenDto(jwt, refreshToken);
    }
 
    private String retrieveJwt(Authentication authentication) {
        return jwTokenGenerator.generate(authentication);
    }

    private String retrieveRefreshToken(Authentication authentication) {
        return refresTokenGenerator.generate(authentication);
    }
}