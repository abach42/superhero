package com.abach42.superhero.ai;

import java.util.List;

public record SuperheroEmbeddedTeamDto(String taskDescription, List<RelevantSuperheroesDto> members) {

    public static SuperheroEmbeddedTeamDto fromSemanticMatches(String taskDescription,
            List<SemanticMatch> semanticMatches) {
        return new SuperheroEmbeddedTeamDto(taskDescription,
                semanticMatches.stream().map(RelevantSuperheroesDto::fromSemanticMatch).toList());
    }
}
