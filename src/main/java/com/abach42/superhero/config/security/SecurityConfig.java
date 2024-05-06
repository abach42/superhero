package com.abach42.superhero.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    @Order(1)
    SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
            .securityMatcher("/api/**")
            .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/api/v1/superheroes/**").authenticated()
                        .requestMatchers("/api/v1/login").authenticated()
                        .anyRequest().authenticated())

            .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt(Customizer.withDefaults()))
            .httpBasic(Customizer.withDefaults());


        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain documentationResourceFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
            .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/chart.html","/swagger-ui/**", "/v3/api-docs/**").permitAll());
        return http.build();
    }
}