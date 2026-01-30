package com.abach42.superhero.ai;

import org.springframework.context.annotation.Profile;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Profile("!test")
@ShellComponent
public class SuperheroVectorCommand {

    private final VectorService vectorService;

    public SuperheroVectorCommand(VectorService vectorService) {
        this.vectorService = vectorService;
    }

    @ShellMethod("Update vector data")
    public void updateSemanticData() {
        vectorService.updateSuperheroes();
    }
}
