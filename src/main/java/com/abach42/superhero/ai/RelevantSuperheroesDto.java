package com.abach42.superhero.ai;

import com.abach42.superhero.superhero.Superhero;
import com.abach42.superhero.superhero.SuperheroDto;

public record RelevantSuperheroesDto(SuperheroDto superhero, double relevance) {

    public static RelevantSuperheroesDto fromSemanticMatch(SemanticMatch semanticMatch) {
        Superhero hero = semanticMatch.superhero();
        SuperheroDto superheroDto = new SuperheroDto(hero.getId(), hero.getAlias(),
                hero.getRealName(), hero.getDateOfBirth(), hero.getGender(), hero.getOccupation(),
                hero.getOriginStory(), null);

        return new RelevantSuperheroesDto(superheroDto, semanticMatch.relevance());
    }
}

