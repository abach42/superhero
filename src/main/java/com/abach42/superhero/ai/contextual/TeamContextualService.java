package com.abach42.superhero.ai.contextual;

import com.abach42.superhero.ai.SemanticSearchException;
import com.abach42.superhero.superhero.SuperheroDto;
import com.abach42.superhero.superhero.SuperheroService;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

@Service
public class TeamContextualService {

    private final ChatService chatService;
    private final SuperheroService superheroService;
    private final BeanOutputConverter<TeamRagResponseDto> outputConverter;

    public TeamContextualService(ChatService chatService, SuperheroService superheroService,
            BeanOutputConverter<TeamRagResponseDto> outputConverter) {
        this.chatService = chatService;
        this.superheroService = superheroService;
        this.outputConverter = outputConverter;
    }

    public SuperheroRagTeamDto generateTeamByRag(String query, int quantity) {
        TeamRagResponseDto teamRagResponseDto = getTeamRagResponse(query, quantity);

        return new SuperheroRagTeamDto(query, teamRagResponseDto.team().stream().map(id ->
                        SuperheroDto.fromDomain(superheroService.getSuperhero(id))).toList(),
                teamRagResponseDto.explanation());
    }

    private TeamRagResponseDto getTeamRagResponse(String query, int quantity) {
        String maybeJson = chatService.callTeamPrompt(query, quantity);

        try {
            return outputConverter.convert(maybeJson);
        } catch (RuntimeException e) {
            throw new SemanticSearchException("Team contextual response could not be rendered: "
                    + maybeJson, e);
        }
    }
}
