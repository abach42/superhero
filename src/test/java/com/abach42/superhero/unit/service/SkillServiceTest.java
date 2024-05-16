package com.abach42.superhero.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.abach42.superhero.dto.SkillDto;
import com.abach42.superhero.entity.Skill;
import com.abach42.superhero.exception.ApiException;
import com.abach42.superhero.repository.SkillRepository;
import com.abach42.superhero.service.SkillService;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService subject;


    @Test
    @DisplayName("Find all throws when empty")
    public void testGetSkillListThrowsWhenEmpty() {
        List<Skill> skills = Collections.emptyList();
        given(skillRepository.findAll()).willReturn(skills);

        ApiException exception = assertThrows(ApiException.class, () -> subject.getSkillList());
        assertThat(HttpStatus.NOT_FOUND).isEqualTo(exception.getStatusCode());
    }

    @Test
    @DisplayName("Get skill can be converted")
    public void testGetSkillConverted() {
        Long skillId = 1L;
        Skill skill = new Skill("Flying");
        given(skillRepository.findById(skillId)).willReturn(Optional.of(skill));

        SkillDto dto = subject.getSkillConverted(skillId);
        assertNotNull(dto);
        assertThat(skill.getName()).isEqualTo(dto.name());
    }
}