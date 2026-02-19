package com.abach42.superhero.ai;

import com.abach42.superhero.superhero.Superhero;
import java.util.stream.Collectors;

public abstract class ContentStrategy {

    abstract String generate(Superhero superhero);

    public String generateSkills(Superhero superhero) {
        return superhero.getSkillProfiles().stream()
                .map(skill -> {
                    String descriptive = switch (skill.getIntensity()) {
                        case 1 -> "is very low (1/5)";
                        case 2 -> "is low (2/5)";
                        case 3 -> "is medium (3/5)";
                        case 4 -> "is high (4/5)";
                        case 5 -> "is very high (5/5)";
                        default -> "is unknown (0/5)";
                    };
                    return "- " + skill.getSkill() + " " + descriptive + "\n";
                })
                .collect(Collectors.joining());
    }
}
