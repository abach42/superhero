package com.abach42.superhero.ai.contextual;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.abach42.superhero.ai.SemanticSearchException;
import com.abach42.superhero.superhero.SuperheroService;
import com.abach42.superhero.testconfiguration.TestStubs;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.converter.BeanOutputConverter;

@ExtendWith(MockitoExtension.class)
class TeamContextualServiceTest {

    @Mock
    private ChatService chatService;

    @Mock
    private SuperheroService superheroService;

    private TeamContextualService subject;

    @BeforeEach
    void setUp() {
        subject = new TeamContextualService(chatService, superheroService,
                new BeanOutputConverter<>(TeamRagResponseDto.class));
    }

    @Test
    @DisplayName("Should map chat json result to contextual team dto")
    void shouldMapChatResult() {
        given(chatService.callTeamPrompt("Rescue city", 2))
                .willReturn("{\"team\":[1],\"explanation\":\"Best fit\"}");
        given(superheroService.getSuperhero(1L)).willReturn(TestStubs.getSuperheroStub());

        SuperheroRagTeamDto result = subject.generateTeamByRag("Rescue city", 2);

        assertThat(result.taskDescription()).isEqualTo("Rescue city");
        assertThat(result.members()).hasSize(1);
        assertThat(result.explanation()).isEqualTo("Best fit");
    }

    @Test
    @DisplayName("Should throw semantic search exception for invalid json")
    void shouldThrowForInvalidJson() {
        given(chatService.callTeamPrompt("Rescue city", 2)).willReturn("not-json");

        assertThatThrownBy(() -> subject.generateTeamByRag("Rescue city", 2))
                .isInstanceOf(SemanticSearchException.class)
                .hasMessageContaining("Team contextual response could not be rendered");
    }
}
