package com.abach42.superhero.ai.contextual;

import com.abach42.superhero.ai.SuperheroShortDto;
import java.util.List;

public record SuperheroRagTeamDto(
        String taskDescription, List<SuperheroShortDto> members, String explanation) {

}
