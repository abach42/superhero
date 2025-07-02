package com.abach42.superhero.service;

import com.abach42.superhero.dto.TokenDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    AbstractTokenGenerator jwTokenGenerator;
    AbstractTokenGenerator refreshTokenGenerator;

    TokenService(AbstractTokenGenerator jwTokenGenerator,
            AbstractTokenGenerator refreshTokenGenerator) {
        this.jwTokenGenerator = jwTokenGenerator;
        this.refreshTokenGenerator = refreshTokenGenerator;
    }

    public TokenDto generateTokenPair(Authentication authentication) {
        String jwt = retrieveJwt(authentication);
        String refreshToken = retrieveRefreshToken(authentication);

        return new TokenDto(jwt, "Bearer", JwtTokenGenerator.EXP * 60, refreshToken);
    }

    private String retrieveJwt(Authentication authentication) {
        return jwTokenGenerator.generate(authentication);
    }

    private String retrieveRefreshToken(Authentication authentication) {
        return refreshTokenGenerator.generate(authentication);
    }
}