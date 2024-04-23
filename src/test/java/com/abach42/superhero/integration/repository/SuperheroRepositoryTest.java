package com.abach42.superhero.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.transaction.annotation.Transactional;

import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.integration.configuration.TestContainerConfiguration;
import com.abach42.superhero.repository.SuperheroRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SuperheroRepositoryTest {

    @Autowired
    private SuperheroRepository subject;

    @Nested
    @DirtiesContext(classMode = ClassMode.AFTER_CLASS)
    @DisplayName("Tests for counting superheroes")
    class CountTests {
        @Test
        @DisplayName("count superheroes dos not count soft deleted")
        void testCountDoesNotCountSoftDeleted() {
            assertThat(subject.count()).isEqualTo(1L);
            assertThat(subject.countByDeletedIsTrue()).isEqualTo(1L);
        }
    
        @Test
        @DisplayName("find all superheroes does not find soft deleted")
        void testFindAllDoesNotFindSoftDeleted() {
            assertThat(subject.findAll()).size().isEqualTo(1);
            assertThat(subject.findAll(Pageable.ofSize(1)).getNumberOfElements()).isEqualTo(1);
        }
    
        @Test
        @DisplayName("find all superheroes does not find soft deleted")
        void testFindByIdDoesNotFindSoftDeleted() {
            assertThat(subject.findById(1L).isPresent()).isTrue();
        }
    }

    @Test
    @DisplayName("delete by deleted is true, erases record of soft deleted")
    @Transactional
    void testDeleteByDeletedErases() {
        subject.deleteByDeletedIsTrue();
        assertThat(subject.count()).isEqualTo(1L);
            assertThat(subject.countByDeletedIsTrue()).isEqualTo(0);
    }

    @Test
    @DisplayName("add new superhero")
    @Transactional
    void testAddNewSuperhero() {
        Superhero superhero = new Superhero("foo", "bar", LocalDate.of(1917, 1, 1), "Male", "foo", "foo");
        subject.save(superhero);
        assertThat(subject.count()).isEqualTo(2L);
    }

    @Test
    @DisplayName("update existing superhero")
    @Transactional
    void testUPdateSuperhero() {
        Superhero superhero = subject.findById(1L).get();
        superhero.setAlias("new");
        Superhero updatedSuperhero = subject.save(superhero);
        assertThat(updatedSuperhero).usingRecursiveComparison().isEqualTo(superhero);
    }
}