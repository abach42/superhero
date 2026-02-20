package com.abach42.superhero.ai.contextual;

import com.abach42.superhero.superhero.Superhero;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class PromptService {

    private final Resource profilingResource;
    private final Resource contextualResource;

    public PromptService(
            @Value("classpath:/prompts/superhero-profiling.st") Resource profilingResource,
            @Value("classpath:/prompts/team-contextual.st") Resource contextualResource) {
        this.profilingResource = profilingResource;
        this.contextualResource = contextualResource;
    }

    public Prompt generateSuperheroProfile(Superhero superhero) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(profilingResource);

        Map<String, Object> params = Map.of(
                "skillsFormatted", generateSkills(superhero),
                "hero", superhero
        );

        return systemPromptTemplate.create(params);
    }

    public Prompt generateContextualPrompt(String task, int teamSize, String candidates) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(contextualResource);

        Map<String, Object> params = Map.of(
                "candidates", candidates,
                "task", task,
                "teamSize", teamSize
        );

        return systemPromptTemplate.create(params);
    }

    private String generateSkills(Superhero superhero) {
        return superhero.getSkillProfiles().stream()
                .map(skill -> {
                    String descriptive = switch (skill.getIntensity()) {
                        case 1 -> "is very low";
                        case 2 -> "is low";
                        case 3 -> "is medium";
                        case 4 -> "is high";
                        case 5 -> "is very high";
                        default -> "is unknown";
                    };
                    return "- " + skill.getSkill() + " " + descriptive + "\n";
                })
                .collect(Collectors.joining());
    }
}