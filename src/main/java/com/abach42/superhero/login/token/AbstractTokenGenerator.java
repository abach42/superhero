package com.abach42.superhero.login.token;

import static com.abach42.superhero.login.authorization.JwtConfig.MAC_ALGORITHM;

import com.abach42.superhero.user.ApplicationUserService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

public abstract class AbstractTokenGenerator {

    public static final String CLAIM_AUTHORITIES = "authorities";

    public static final String CLAIM_ALLOWED = "allowed";

    public final ApplicationUserService applicationUserService;

    private final JwtEncoder jwtEncoder;

    protected Instant now;

    public AbstractTokenGenerator(ApplicationUserService applicationUserService,
            JwtEncoder jwtEncoder) {
        this.applicationUserService = applicationUserService;
        this.jwtEncoder = jwtEncoder;
    }

    abstract TokenPurpose getAllowedAction();

    abstract int getExpirationMinutes();

    public String generateToken(Authentication authentication) {
        now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(getExpirationMinutes(), ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim(CLAIM_AUTHORITIES, getRole(authentication))
                .claim(CLAIM_ALLOWED, getAllowedAction())
                .build();

        return this.jwtEncoder.encode(
                JwtEncoderParameters.from(JwsHeader.with(MAC_ALGORITHM).build(),
                        claims)).getTokenValue();
    }

    private String getRole(Authentication authentication) {
        // getting roles from database again, and override jwt
        // so that it can be actualized and not kept by
        // JWT -> refresh token -> JWT -> refresh token forever.
        return applicationUserService.retrieveUserByEmail(authentication.getName()).getRole()
                .name();
    }
}