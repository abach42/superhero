package com.abach42.superhero.config.security;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AppConfiguration.CorsConfigurationProperties.class)
public class AppConfiguration {

    private final CorsConfigurationProperties configurationPropertyRecord;

    public AppConfiguration(CorsConfigurationProperties configurationPropertyRecord) {
        this.configurationPropertyRecord = configurationPropertyRecord;
    }

    @Bean
    CorsConfigurationProperties configurationProperties() {
        return configurationPropertyRecord;
    }

    @ConfigurationProperties(prefix = "com.abach42.superhero.cors")
    public record CorsConfigurationProperties(
            List<String> allowedOrigins,
            List<String> allowedMethods,
            List<String> allowedHeaders,
            List<String> exposedHeaders,
            Boolean allowCredentials,
            Long maxAge) {
    }
}
