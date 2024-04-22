package com.abach42.superhero.integration.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class TestContainerConfiguration {
    //TODO check this
    @Value("${abach42.superhero.oci.testcontainer.imageName:postgres:latest}")
    private String dockerImageName;

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(dockerImageName);
    }
}