package com.abach42.superhero.ai.contextual;

import com.abach42.superhero.superhero.SuperheroDto;
import java.util.List;

public record SuperheroRagTeamDto(
        String taskDescription, List<SuperheroDto> members, String reasoning) {

}
