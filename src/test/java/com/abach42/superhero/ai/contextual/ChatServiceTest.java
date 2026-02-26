package com.abach42.superhero.ai.contextual;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import com.abach42.superhero.ai.SemanticMatch;
import com.abach42.superhero.ai.TeamService;
import com.abach42.superhero.testconfiguration.TestStubs;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private TeamService teamService;

    @Mock
    private PromptService promptService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should generate team response text from chat client")
    void shouldGenerateTeamResponseText() throws JsonProcessingException {
        ChatClient.Builder builder = org.mockito.Mockito.mock(ChatClient.Builder.class);
        ChatClient chatClient = org.mockito.Mockito.mock(ChatClient.class, RETURNS_DEEP_STUBS);
        given(builder.build()).willReturn(chatClient);

        var hero = TestStubs.getSuperheroStub();
        SemanticMatch candidate = new SemanticMatch(hero, 0.8);
        Prompt heroPrompt = new Prompt("Candidate profile");
        Prompt teamPrompt = new Prompt("Final prompt");

        given(teamService.retrieveEmbeddingTeam(eq("Rescue city"), any()))
                .willReturn(List.of(candidate));
        given(promptService.generateSuperheroProfile(hero)).willReturn(heroPrompt);
        given(promptService.generateContextualPrompt("Rescue city", 5,
                "Candidate profile"))
                .willReturn(teamPrompt);
        given(Objects.requireNonNull(chatClient.prompt(teamPrompt).call().chatResponse()).
                getResult().getOutput().getText()).willReturn(objectMapper.writeValueAsString(
                        new TeamRagResponseDto(Set.of(1L), "ok")));

        ChatService subject = new ChatService(teamService, promptService, builder);

        String result = subject.callTeamPrompt("Rescue city", 5);

        assertThat(result).contains("team");
    }
}
