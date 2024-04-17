package com.abach42.superhero.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Import;

import com.abach42.superhero.integration.configuration.TestContainerConfiguration;
import com.abach42.superhero.repository.SuperheroRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
public class SuperheroRepositoryTest {

    @Autowired
    private SuperheroRepository superheroRepository;

    @Test
    @Description("find a superhero on find all")
    void testFindAll() {
        assertThat(superheroRepository.count()).isGreaterThan(0L);
    }
}