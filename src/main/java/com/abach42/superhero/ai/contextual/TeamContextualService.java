package com.abach42.superhero.ai.contextual;

import com.abach42.superhero.ai.SemanticSearchException;
import com.abach42.superhero.superhero.SuperheroDto;
import com.abach42.superhero.superhero.SuperheroService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class TeamContextualService {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final SuperheroService superheroService;

    public TeamContextualService(ChatService chatService, ObjectMapper objectMapper,
            SuperheroService superheroService) {
        this.chatService = chatService;
        this.objectMapper = objectMapper;
        this.superheroService = superheroService;
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
            return objectMapper.readValue(maybeJson, TeamRagResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new SemanticSearchException("Team contextual response could not be rendered: "
                    + maybeJson, e);
        }
    }
}
