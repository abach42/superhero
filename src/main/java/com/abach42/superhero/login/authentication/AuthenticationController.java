package com.abach42.superhero.login.authentication;

import com.abach42.superhero.shared.api.PathConfig;
import com.abach42.superhero.login.token.TokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication")
@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping(path = PathConfig.AUTH)
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Authenticate to get authorization")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Authenticated",
                    content = @Content,
                    headers = @Header(
                            name = "Authorization",
                            required = true,
                            description = "Set email and password to Authorization header."
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    @GetMapping("/login")
    public ResponseEntity<TokenResponseDto> showTokenByBasicAuth(Authentication authentication) {
        TokenResponseDto tokenResponseDto = authenticationService.createNewTokenPair(
                authentication);
        return ResponseEntity.ok().body(tokenResponseDto);
    }

    @Operation(summary = "Send a refresh token, get a new jwt")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Authenticated",
                    content = @Content,
                    headers = @Header(
                            name = "Authorization",
                            required = true,
                            description = "Send a refresh token, get a new jwt."
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    @GetMapping("/refresh-token")
    public ResponseEntity<TokenResponseDto> showTokenByToken(Authentication authentication) {
        TokenResponseDto tokenResponseDto = authenticationService.createNewTokenPair(
                authentication);
        return ResponseEntity.ok().body(tokenResponseDto);
    }
}