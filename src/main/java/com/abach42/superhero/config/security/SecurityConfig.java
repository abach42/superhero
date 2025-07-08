package com.abach42.superhero.config.security;

import com.abach42.superhero.config.api.PathConfig;
import com.abach42.superhero.config.security.AppConfiguration.CorsConfigurationProperties;
import com.abach42.superhero.login.token.AbstractTokenGenerator;
import com.abach42.superhero.login.token.TokenPurpose;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain apiFilterChain(HttpSecurity http,
            Function<String, CorsConfigurationSource> generateCorsConfig,
            Function<TokenPurpose, WebExpressionAuthorizationManager> tokenAccess)
            throws Exception {
        String securityMatcherPattern = PathConfig.BASE_URI + "/**";

        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(
                    session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .securityMatcher(securityMatcherPattern)
            .cors((cors) -> cors
                    .configurationSource(generateCorsConfig.apply(securityMatcherPattern)))
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(PathConfig.BASE_URI + "/auth/login").authenticated()
                    .requestMatchers(PathConfig.BASE_URI + "/auth/refresh-token")
                            .access(tokenAccess.apply(TokenPurpose.REFRESH))
                    .requestMatchers(PathConfig.BASE_URI + "/**")
                            .access(tokenAccess.apply(TokenPurpose.AUTH))
                    .anyRequest().denyAll()
            )
            .oauth2ResourceServer((oauth2) -> oauth2
                    .jwt(Customizer.withDefaults()))
            .httpBasic(Customizer.withDefaults())
            .headers((headers) -> headers
                    .frameOptions(FrameOptionsConfig::sameOrigin));

        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain documentationResourceFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/chart.html", "/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll());
        return http.build();
    }

    @Bean
    public Function<TokenPurpose, WebExpressionAuthorizationManager> tokenAccess() {
        return tokenPurpose -> new WebExpressionAuthorizationManager(
                "principal.claims['" + AbstractTokenGenerator.CLAIM_ALLOWED + "'] == '"
                        + tokenPurpose.name() + "'");
    }

    @Bean
    Function<String, CorsConfigurationSource> generateCorsConfig(
            CorsConfigurationProperties configurationProperties) {
        return securityMatcherPattern -> {
            CorsConfiguration configuration = new CorsConfiguration();

            configuration.setAllowedOrigins(configurationProperties.allowedOrigins());
            configuration.setAllowedMethods(configurationProperties.allowedMethods());
            configuration.setAllowedHeaders(configurationProperties.allowedHeaders());
            configuration.setExposedHeaders(configurationProperties.exposedHeaders());
            configuration.setAllowCredentials(configurationProperties.allowCredentials());

            configuration.setMaxAge(configuration.getMaxAge());

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration(securityMatcherPattern, configuration);
            return source;
        };
    }
}