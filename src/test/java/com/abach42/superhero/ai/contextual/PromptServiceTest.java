package com.abach42.superhero.ai.contextual;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.skill.Skill;
import com.abach42.superhero.skillprofile.SkillProfile;
import com.abach42.superhero.testconfiguration.TestStubs;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.io.ByteArrayResource;

class PromptServiceTest {

    @Test
    @DisplayName("Should render superhero profile with descriptive skill intensity")
    void shouldRenderSuperheroProfile() {
        String profileTemplate = "Hero: {hero.alias}\n{skillsFormatted}";
        String contextualTemplate = "Task: {task} Team: {teamSize} Candidates: {candidates}";
        PromptService subject = new PromptService(
                new ByteArrayResource(profileTemplate.getBytes()),
                new ByteArrayResource(contextualTemplate.getBytes()),
                new BeanOutputConverter<>(TeamRagResponseDto.class));

        var hero = TestStubs.getSuperheroStub();
        hero.setSkillProfiles(List.of(new SkillProfile(1L, 5, new Skill(6L, "courage"))));

        String content = subject.generateSuperheroProfile(hero).getContents();

        assertThat(content).contains("Hero: " + hero.getAlias());
        assertThat(content).contains("courage is very high");
    }

    @Test
    @DisplayName("Should render contextual prompt with task and candidates")
    void shouldRenderContextualPrompt() {
        String profileTemplate = "Hero: {hero.alias}\n{skillsFormatted}";
        String contextualTemplate = "Task: {task} Team: {teamSize} Candidates: {candidates}";
        PromptService subject = new PromptService(
                new ByteArrayResource(profileTemplate.getBytes()),
                new ByteArrayResource(contextualTemplate.getBytes()),
                new BeanOutputConverter<>(TeamRagResponseDto.class));

        String content = subject.generateContextualPrompt("Rescue city", 3, "- Hero A").getContents();

        assertThat(content).contains("Task: Rescue city");
        assertThat(content).contains("Team: 3");
        assertThat(content).contains("Candidates: - Hero A");
    }
}
