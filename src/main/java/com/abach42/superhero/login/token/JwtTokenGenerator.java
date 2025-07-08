package com.abach42.superhero.login.token;

import com.abach42.superhero.user.ApplicationUserService;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenGenerator extends AbstractTokenGenerator {

    public JwtTokenGenerator(ApplicationUserService applicationUserService,
            JwtEncoder jwtEncoder) {
        super(applicationUserService, jwtEncoder);
    }

    @Override
    TokenPurpose getAllowedAction() {
        return TokenPurpose.AUTH;
    }

    @Override
    public int getExpirationMinutes() {
        return 15;
    }
}
