package com.abach42.superhero.skill;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.testconfiguration.TestStubs;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags(value = {@Tag("unit"), @Tag("skill")})
public class SkillListDtoTest {

    @Test
    @DisplayName("Skills can be mapped to their DTOs")
    public void testSkillList() {
        SkillDto skill1 = SkillDto.fromDomain(TestStubs.getSkillStub());
        SkillDto skill2 = SkillDto.fromDomain(TestStubs.getSkillStub());

        SkillListDto actual = new SkillListDto(List.of(skill1, skill2));

        Assertions.assertNotNull(actual.skills());
        assertThat(actual.skills().size()).isEqualTo(2);
    }
}