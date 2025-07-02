package com.abach42.superhero.unit.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.configuration.TestDataConfiguration;
import com.abach42.superhero.dto.SkillDto;
import com.abach42.superhero.dto.SkillListDto;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SkillListDtoTest {

    @Test
    @DisplayName("Skills can be mapped to their DTOs")
    public void testSkillList() {
        SkillDto skill1 = SkillDto.fromDomain(TestDataConfiguration.getSkillStub());
        SkillDto skill2 = SkillDto.fromDomain(TestDataConfiguration.getSkillStub());

        SkillListDto actual = new SkillListDto(List.of(skill1, skill2));

        Assertions.assertNotNull(actual.getSkills());
        assertThat(actual.getSkills().size()).isEqualTo(2);
    }
}