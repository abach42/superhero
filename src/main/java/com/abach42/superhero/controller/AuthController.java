package com.abach42.superhero.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abach42.superhero.config.api.PathConfig;
import com.abach42.superhero.service.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Superhero authentication")
@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping(path = PathConfig.TOKENS)
public class AuthController {
    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Operation(summary = "Authenticate to get authorization")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "Authenticated",
            content = @Content,
            headers =  @Header(
                name = "Authorization", 
                required = true, 
                description = "Set email and password to Authorization header."
                )
            ),
        @ApiResponse( 
            responseCode = "401",
            description = "Unauthorized",
            content =  @Content
        )
    })
    @GetMapping("/login")
    public ResponseEntity<String> showToken(Authentication authentication) {
        String token = tokenService.generateToken(authentication);
        return ResponseEntity.ok().body(token);
    }
}