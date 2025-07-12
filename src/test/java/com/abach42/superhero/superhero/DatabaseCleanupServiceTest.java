package com.abach42.superhero.superhero;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.testconfiguration.TestContainerConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@Tags(value = {@Tag("integration"), @Tag("superhero"), @Tag("cleanup")})
@SpringBootTest(classes = {TestContainerConfiguration.class}, properties = {
        "com.abach42.superhero.database-cleanup-service.enabled=true"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DatabaseCleanupServiceTest {

    @Autowired
    private SuperheroRepository superheroRepository;

    @Autowired
    private DatabaseCleanupService databaseCleanupService;

    @Test
    @Transactional
    void shouldDeleteSoftDeletedSuperheroes() {
        assertThat(superheroRepository.countByDeletedIsFalse()).isEqualTo(1);
        assertThat(superheroRepository.countByDeletedIsTrue()).isEqualTo(2);

        databaseCleanupService.eraseRecordsMarkedAsDeleted();

        assertThat(superheroRepository.countByDeletedIsFalse()).isEqualTo(1);
        assertThat(superheroRepository.countByDeletedIsTrue()).isZero();
        assertThat(superheroRepository.findAll()).extracting("alias")
                .containsExactly("foo");
    }
}
