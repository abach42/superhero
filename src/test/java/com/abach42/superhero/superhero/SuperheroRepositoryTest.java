package com.abach42.superhero.superhero;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.testconfiguration.TestContainerConfiguration;
import com.abach42.superhero.testconfiguration.TestStubs;
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
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SuperheroRepositoryTest {

    @Autowired
    private SuperheroRepository subject;

    @Test
    @DisplayName("delete by deleted is true, erases record of soft deleted")
    @Transactional
    public void testDeleteByDeletedErases() {
        subject.deleteByDeletedIsTrue();
        assertThat(subject.count()).isEqualTo(1L);
        assertThat(subject.countByDeletedIsTrue()).isEqualTo(0);
    }

    @Test
    @DisplayName("add new superhero")
    @Transactional
    public void testAddNewSuperhero() {
        Superhero superheroStub = TestStubs.getSuperheroStubWithPassword();
        subject.save(superheroStub);

        assertThat(subject.count()).isEqualTo(2L);
    }

    @Test
    @DisplayName("update existing superhero")
    @Transactional
    public void testUpdateSuperhero() {
        //assuming Superhero.getId() == 1L is in test database
        Superhero superhero = subject.findById(1L).get();
        superhero.setAlias("new");
        Superhero updatedSuperhero = subject.save(superhero);
        assertThat(updatedSuperhero).usingRecursiveComparison().isEqualTo(superhero);
    }

    @Nested
    @NestedTestConfiguration(EnclosingConfiguration.OVERRIDE)
    @DirtiesContext(classMode = ClassMode.AFTER_CLASS)
    @DisplayName("Tests for counting superheroes")
    class CountTest {

        @Test
        @DisplayName("count superheroes does not count soft deleted")
        public void testCountDoesNotCountSoftDeleted() {
            assertThat(subject.count()).isEqualTo(1L);
            assertThat(subject.countByDeletedIsTrue()).isEqualTo(2L);
        }

        @Test
        @DisplayName("find all superheroes does not find soft deleted")
        public void testFindAllDoesNotFindSoftDeleted() {
            assertThat(subject.findAll()).size().isEqualTo(1);
            assertThat(subject.findAll(Pageable.ofSize(1)).getNumberOfElements())
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("find all superheroes does not find soft deleted")
        public void testFindByIdDoesNotFindSoftDeleted() {
            assertThat(subject.findById(1L).isPresent()).isTrue();
            assertThat(subject.findById(2L).isPresent()).isFalse();
        }
    }
}