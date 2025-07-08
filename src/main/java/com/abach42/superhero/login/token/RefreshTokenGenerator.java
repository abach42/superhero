package com.abach42.superhero.login.token;

import com.abach42.superhero.user.ApplicationUserService;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenGenerator extends AbstractTokenGenerator {

    public RefreshTokenGenerator(ApplicationUserService applicationUserService,
            JwtEncoder jwtEncoder) {
        super(applicationUserService, jwtEncoder);
    }

    @Override
    TokenPurpose getAllowedAction() {
        return TokenPurpose.REFRESH;
    }

    @Override
    int getExpirationMinutes() {
        return 120;
    }
}
