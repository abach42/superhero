package com.abach42.superhero.ai;

import com.abach42.superhero.superhero.Superhero;

public record RelevantSuperheroesDto(SuperheroShortDto superhero, double relevance) {

    public static RelevantSuperheroesDto fromSemanticMatch(SemanticMatch semanticMatch) {
        Superhero hero = semanticMatch.superhero();
        SuperheroShortDto superheroShortDto = new SuperheroShortDto(hero.getId(), hero.getAlias(),
                hero.getRealName(), hero.getDateOfBirth(), hero.getGender(), hero.getOccupation(),
                hero.getOriginStory());

        return new RelevantSuperheroesDto(superheroShortDto, semanticMatch.relevance());
    }
}

