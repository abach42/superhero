package com.abach42.superhero.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.abach42.superhero.integration.configuration.TestContainerConfiguration;
import com.abach42.superhero.repository.SuperheroRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SuperheroRepositoryTest {

    @Autowired
    private SuperheroRepository superheroRepository;

    @Test
    @DisplayName("find all superheroes counts more than 0")
    void testFindAll() {
        assertThat(superheroRepository.count()).isGreaterThan(0L);
    }
}