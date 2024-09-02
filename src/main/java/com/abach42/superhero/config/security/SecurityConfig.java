package com.abach42.superhero.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.abach42.superhero.config.security.AppConfiguration.CorsConfigurationProperties;
import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasScope;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    CorsConfigurationProperties configurationProperties;

    @Bean
    @Order(1)
    SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors((cors) -> cors
                        .configurationSource(corsConfigurationSource()))
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/login").authenticated()
                        .requestMatchers("api/v1/refresh-token").access(hasScope("refresh"))
                        .requestMatchers("/api/v1/superheroes").access(hasScope("action"))
                        .requestMatchers("/api/v1/superheroes/**").access(hasScope("action"))
                        .anyRequest().denyAll())
                .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt(Customizer.withDefaults()))
                .httpBasic(Customizer.withDefaults())
                .headers((headers) -> headers
                        .frameOptions((frameOptions) -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain documentationResourceFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/chart.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll());
        return http.build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(configurationProperties.allowedOrigins());
        configuration.setAllowedMethods(configurationProperties.allowedMethods());
        configuration.setAllowedHeaders(configurationProperties.allowedHeaders());
        configuration.setExposedHeaders(configurationProperties.exposedHeaders());
        configuration.setAllowCredentials(configurationProperties.allowCredentials());
        configuration.setMaxAge(configuration.getMaxAge());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}