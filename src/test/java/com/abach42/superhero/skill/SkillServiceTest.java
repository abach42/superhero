package com.abach42.superhero.skill;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.abach42.superhero.shared.api.ApiException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@Tags(value = {@Tag("unit"), @Tag("skill")})
@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    private SkillService subject;

    @BeforeEach
    void setUp() {
        subject = new SkillService(skillRepository);
    }

    @Test
    @DisplayName("Should return skill list when skills exist")
    void shouldReturnSkillListWhenSkillsExist() {
        List<Skill> skills = List.of(new Skill("Flying"), new Skill("Super Strength"));
        given(skillRepository.findAll()).willReturn(skills);

        SkillListDto result = subject.getSkillList();

        assertNotNull(result);
        assertThat(result.skills()).hasSize(2);
        assertThat(result.skills().get(0).name()).isEqualTo("Flying");
        assertThat(result.skills().get(1).name()).isEqualTo("Super Strength");
    }

    @Test
    @DisplayName("Should throw not found when no skills exist")
    void shouldThrowNotFoundWhenNoSkillsExist() {
        given(skillRepository.findAll()).willReturn(Collections.emptyList());

        ApiException exception = assertThrows(ApiException.class, () -> subject.getSkillList());

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).contains(SkillService.SKILLS_NOT_FOUND_MSG);
    }

    @Test
    @DisplayName("Should convert skill to DTO")
    void shouldConvertSkillToDto() {
        Long skillId = 1L;
        Skill skill = new Skill("Teleportation");
        given(skillRepository.findById(skillId)).willReturn(Optional.of(skill));

        SkillDto result = subject.getSkillConverted(skillId);

        assertNotNull(result);
        assertThat(result.name()).isEqualTo("Teleportation");
    }

    @Test
    @DisplayName("Should throw not found when skill doesn't exist for conversion")
    void shouldThrowNotFoundWhenSkillNotExistsForConversion() {
        Long skillId = 999L;
        given(skillRepository.findById(skillId)).willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.getSkillConverted(skillId));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).contains(SkillService.SKILL_NOT_FOUND_MSG);
        assertThat(exception.getMessage()).contains(skillId.toString());
    }

    @Test
    @DisplayName("Should return skill when it exists")
    void shouldReturnSkillWhenExists() {
        Long skillId = 1L;
        Skill skill = new Skill("Mind Reading");
        given(skillRepository.findById(skillId)).willReturn(Optional.of(skill));

        Skill result = subject.getSkill(skillId);

        assertNotNull(result);
        assertThat(result.getName()).isEqualTo("Mind Reading");
    }

    @Test
    @DisplayName("Should throw not found when skill doesn't exist")
    void shouldThrowNotFoundWhenSkillNotExists() {
        Long skillId = 404L;
        given(skillRepository.findById(skillId)).willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.getSkill(skillId));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).contains(SkillService.SKILL_NOT_FOUND_MSG);
        assertThat(exception.getMessage()).contains(skillId.toString());
    }
}