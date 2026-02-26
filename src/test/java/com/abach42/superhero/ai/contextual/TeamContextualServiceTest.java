package com.abach42.superhero.ai.contextual;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;

import com.abach42.superhero.ai.Query;
import com.abach42.superhero.ai.SemanticSearchException;
import com.abach42.superhero.superhero.SuperheroService;
import com.abach42.superhero.testconfiguration.TestStubs;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.converter.BeanOutputConverter;

@ExtendWith(MockitoExtension.class)
class TeamContextualServiceTest {

    @Mock
    private ChatService chatService;

    @Mock
    private SuperheroService superheroService;

    @Mock
    private BeanOutputConverter<TeamRagResponseDto> outputConverter;

    @InjectMocks
    private TeamContextualService subject;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should map chat json result to contextual team dto")
    void shouldMapChatResult() throws JsonProcessingException {
        TeamRagResponseDto teamRagResponseDto =
                new TeamRagResponseDto(Set.of(1L), "Best fit");
        String promptResult = objectMapper.writeValueAsString(
                teamRagResponseDto);

        given(chatService.callTeamPrompt(argThat(query ->
                query.task().equals("Rescue city") && query.quantity() == 2
        ))).willReturn(promptResult);

        given(outputConverter.convert(promptResult)).willReturn(teamRagResponseDto);
        given(superheroService.getSuperhero(1L)).willReturn(TestStubs.getSuperheroStub());

        SuperheroRagTeamDto result = subject.generateTeamByRag("Rescue city", 2);

        assertThat(result.taskDescription()).isEqualTo("Rescue city");
        assertThat(result.members()).hasSize(1);
        assertThat(result.explanation()).isEqualTo("Best fit");
    }

    @Test
    @DisplayName("Should throw semantic search exception for invalid json")
    void shouldThrowForInvalidJson() {
        given(chatService.callTeamPrompt(argThat(query ->
                query.task().equals("Rescue city") && query.quantity() == 9)))
                .willReturn("not-json");

        given(outputConverter.convert("not-json"))
                .willThrow(new RuntimeException("Parsing failed"));

        assertThatThrownBy(() -> subject.generateTeamByRag("Rescue city", 9))
                .isInstanceOf(SemanticSearchException.class)
                .hasMessageContaining("Team contextual response could not be rendered");
    }
}
