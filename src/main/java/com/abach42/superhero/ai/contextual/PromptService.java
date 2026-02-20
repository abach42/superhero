package com.abach42.superhero.ai.contextual;

import com.abach42.superhero.superhero.Superhero;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class PromptService {

    private final Resource profilingResource;
    private final Resource contextualResource;
    private final BeanOutputConverter<TeamRagResponseDto> outputConverter;

    public PromptService(
            @Value("classpath:/prompts/superhero-profiling.st") Resource profilingResource,
            @Value("classpath:/prompts/team-contextual.st") Resource contextualResource,
            BeanOutputConverter<TeamRagResponseDto> outputConverter) {
        this.profilingResource = profilingResource;
        this.contextualResource = contextualResource;
        this.outputConverter = outputConverter;
    }

    public Prompt generateSuperheroProfile(Superhero superhero) {
        Map<String, Object> params = Map.of(
                "skillsFormatted", generateSkills(superhero),
                "hero", superhero
        );

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(profilingResource);
        return systemPromptTemplate.create(params);
    }

    public Prompt generateContextualPrompt(String task, int teamSize, String candidates) {
        Map<String, Object> params = Map.of(
                "candidates", candidates,
                "task", task,
                "teamSize", teamSize
        );

        OllamaChatOptions ollamaChatOptions =
                OllamaChatOptions.builder().format(outputConverter.getJsonSchemaMap()).build();

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(contextualResource);
        return systemPromptTemplate.create(params, ollamaChatOptions);
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