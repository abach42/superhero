package com.abach42.superhero.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.abach42.superhero.entity.dto.UserDto;
import com.abach42.superhero.service.SuperheroUserService;

@Configuration(proxyBeanMethods = false)
public class UserDetailsServiceConfig {

    @Bean
    UserDetailsService userDetailsService(SuperheroUserService superheroUserService) {
        return email -> {
            UserDto userDto = superheroUserService.getSuperheroConverted(email);
            return User.withUsername(userDto.email())
                .password(userDto.password())
                .roles(userDto.role())
                .build();
        };
    }
}