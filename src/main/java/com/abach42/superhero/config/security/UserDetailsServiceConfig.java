package com.abach42.superhero.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.abach42.superhero.dto.SuperheroUserDto;
import com.abach42.superhero.service.SuperheroUserService;

@Configuration(proxyBeanMethods = false)
public class UserDetailsServiceConfig {

    @Bean
    public UserDetailsService userDetailsService(SuperheroUserService superheroUserService) {
        return email -> {
            SuperheroUserDto userDto = superheroUserService.retrieveSuperheroUser(email);
            return User.withUsername(userDto.email())
                .password(userDto.password())
                .roles(userDto.role())
                .build();
        };
    }
}