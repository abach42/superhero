package com.abach42.superhero.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.abach42.superhero.configuration.TestContainerConfiguration;
import com.abach42.superhero.entity.SkillProfile;
import com.abach42.superhero.repository.SkillProfileRepository;

/*
 * Test real database starting docker container
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class SkillProfileRepositoryTest {
    @Autowired
    private SkillProfileRepository subject;

    @Test
    @DisplayName("find by superhero id returns skill profiles ordered by skill id")
    void testFindBySuperheroIdOrderBySkillId() {
        List<SkillProfile> actual = subject.findBySuperheroIdOrderBySkillId(1L);
        assertThat(actual.size()).isEqualTo(2);

        assertThat(actual.get(1).getSkill().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("find by superhero id and skill id returns one skill")
    void testFindBySuperheroIdAndSkillId() {
        Optional<SkillProfile> actual = subject.findBySuperheroIdAndSkillId(1L, 1L);

        assertThat(actual).isNotEmpty();
    }
}
