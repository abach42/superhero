package com.abach42.superhero.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenGenerators {
    @Bean
    AbstractTokenGenerator jwTokenGenerator() {
        return new JwtTokenGenerator();
    }

    @Bean
    AbstractTokenGenerator refresTokenGenerator() {
        return new RefreshTokenGenerator();
    }

    
}
