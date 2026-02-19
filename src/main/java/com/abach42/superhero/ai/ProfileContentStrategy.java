package com.abach42.superhero.ai;

import com.abach42.superhero.superhero.Superhero;
import org.springframework.stereotype.Component;

@Component(ProfileContentStrategy.QUALIFIER)
public class ProfileContentStrategy extends ContentStrategy {

    public static final String QUALIFIER = "profileContent";

    @Override
    public String generate(Superhero superhero) {
        return generateSkills(superhero);
    }
}
