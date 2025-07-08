package com.abach42.superhero.login.authentication;

import com.abach42.superhero.login.token.JwtTokenGenerator;
import com.abach42.superhero.login.token.RefreshTokenGenerator;
import com.abach42.superhero.login.token.TokenResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final JwtTokenGenerator jwtTokenGenerator;

    private final RefreshTokenGenerator refreshTokenGenerator;

    public AuthenticationService(
            JwtTokenGenerator jwTokenGenerator, RefreshTokenGenerator refreshTokenGenerator) {
        this.jwtTokenGenerator = jwTokenGenerator;
        this.refreshTokenGenerator = refreshTokenGenerator;
    }

    public TokenResponseDto createNewTokenPair(Authentication authentication) {
        String jwt = jwtTokenGenerator.generateToken(authentication);
        String refreshToken = refreshTokenGenerator.generateToken(authentication);

        return new TokenResponseDto(
                jwt,
                TokenResponseDto.TokenType.BEARER,
                jwtTokenGenerator.getExpirationMinutes() * 60,
                refreshToken);
    }
}
