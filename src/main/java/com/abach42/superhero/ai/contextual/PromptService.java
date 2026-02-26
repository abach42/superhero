package com.abach42.superhero.ai.contextual;

import com.abach42.superhero.ai.Query;
import com.abach42.superhero.skillprofile.SkillProfile;
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
                "skillsFormatted", buildSkillRepresentation(superhero),
                "hero", superhero
        );

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(profilingResource);
        return systemPromptTemplate.create(params);
    }

    public Prompt generateContextualPrompt(Query query, String candidates) {
        Map<String, Object> params = Map.of(
                "candidates", candidates,
                "task", query.task(),
                "teamSize", query.quantity()
        );

        OllamaChatOptions ollamaChatOptions =
                OllamaChatOptions.builder().format(outputConverter.getJsonSchemaMap()).build();

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(contextualResource);
        return systemPromptTemplate.create(params, ollamaChatOptions);
    }

    /**
     * representation: enrich numeric values with deterministic natural language.
     */
    private String buildSkillRepresentation(Superhero hero) {
        String skills = hero.getSkillProfiles().stream()
                .map(this::toEmbeddingSkillLine)
                .collect(Collectors.joining("\n"));

        return "Normalized skill scale (1 to 5)\n" + skills;
    }

    private String toEmbeddingSkillLine(SkillProfile skillProfile) {
        Integer intensity = skillProfile.getIntensity();
        String level = normalizeLevel(intensity);
        String labels = comparableLabels(intensity);

        return "Skill: " + skillProfile.getSkill().getName() + " | "
                + "Level: " + intensity + "/5 | "
                + "Interpretation: " + level + " | "
                + "Comparable labels: " + labels;
    }

    private String normalizeLevel(Integer intensity) {
        return switch (intensity) {
            case 1 -> "very low";
            case 2 -> "low";
            case 3 -> "medium";
            case 4 -> "high";
            case 5 -> "very high";
            default -> "unknown";
        };
    }

    private String comparableLabels(Integer intensity) {
        return switch (intensity) {
            case 1 -> "minimal, weak, beginner";
            case 2 -> "limited, basic, developing";
            case 3 -> "solid, moderate, capable";
            case 4 -> "strong, advanced, excellent";
            case 5 -> "outstanding, top-tier, elite";
            default -> "unclassified";
        };
    }
}