package com.abach42.superhero.skill;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.testconfiguration.TestContainerConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class SkillRepositoryTest {

    @Autowired
    private SkillRepository subject;

    @Test
    @DisplayName("count skills available")
    public void testCountDoesNotCountSoftDeleted() {
        assertThat(subject.count()).isEqualTo(3L);
    }
}