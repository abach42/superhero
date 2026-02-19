package com.abach42.superhero.ai;

import com.abach42.superhero.superhero.Superhero;
import java.util.stream.Collectors;

public abstract class ContentStrategy {

    abstract String generate(Superhero superhero);

    public String generateSkills(Superhero superhero) {
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
