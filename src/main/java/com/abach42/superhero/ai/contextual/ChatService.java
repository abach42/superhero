package com.abach42.superhero.ai.contextual;

import com.abach42.superhero.ai.SemanticMatch;
import com.abach42.superhero.ai.TeamService;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final TeamService teamService;
    private final PromptService promptService;
    private final ChatClient chatClient;

    public ChatService(TeamService teamService, PromptService promptService,
            Builder chatClientBuilder) {
        this.teamService = teamService;
        this.promptService = promptService;
        this.chatClient = chatClientBuilder.build();
    }

    public String callTeamPrompt(String query, int quantity) {
        Prompt prompt = generateTeamPrompt(query, quantity);
        return Objects.requireNonNull(chatClient.prompt(prompt).call().chatResponse())
                .getResult().getOutput().getText();
    }

    private Prompt generateTeamPrompt(String query, int quantity) {
        List<SemanticMatch> candidateSuperheroes = getEmbeddingCandidates(query, quantity);

        String candidates = candidateSuperheroes.stream()
                .map(this::getCandidate)
                .collect(Collectors.joining());

        return promptService.generateContextualPrompt(query, quantity, candidates);
    }

    private String getCandidate(SemanticMatch semanticMatch) {
        Prompt prompt = promptService.generateSuperheroProfile(semanticMatch.superhero());
        return prompt.getContents();
    }

    private List<SemanticMatch> getEmbeddingCandidates(String query, int quantity) {
        return teamService.retrieveEmbeddingTeam(query, () -> quantity * 2); //todo quantity
    }
}
