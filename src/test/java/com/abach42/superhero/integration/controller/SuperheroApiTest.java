package com.abach42.superhero.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.abach42.superhero.config.PathConfig;
import com.abach42.superhero.entity.dto.SuperheroListDto;
import com.abach42.superhero.integration.configuration.TestContainerConfiguration;

/*
 * End to end test with database and real client
 * TODO: delete - this one just exists as documentation in repository
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestContainerConfiguration.class)
public class SuperheroApiTest {
    private final static String PATH = PathConfig.SUPERHEROES;
    
    @LocalServerPort
    private Integer port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testRequest() {
        ResponseEntity<SuperheroListDto> responseEntity = restTemplate.getForEntity(PATH, SuperheroListDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
