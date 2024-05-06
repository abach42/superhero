package com.abach42.superhero.unit.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.abach42.superhero.configuration.TestDataConfiguration;
import com.abach42.superhero.dto.SkillDto;
import com.abach42.superhero.dto.SkillListDto;

public class SkillListDtoTest {

    @Test
    @DisplayName("Skills can be mapped to their DTOs")
    public void testSkillList() {
        SkillDto skill1 = SkillDto.fromDomain(TestDataConfiguration.getSkillStub());
        SkillDto skill2 = SkillDto.fromDomain(TestDataConfiguration.getSkillStub());
        
        SkillListDto actual = new SkillListDto(List.of(skill1, skill2));

        assertThat(actual.getSkills().size()).isEqualTo(2);
    }
}