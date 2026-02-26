package com.abach42.superhero.ai.contextual;

import com.abach42.superhero.ai.Query;
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

    public String callTeamPrompt(Query query) {
        Prompt prompt = generateTeamPrompt(query);
        return Objects.requireNonNull(chatClient.prompt(prompt).call().chatResponse())
                .getResult().getOutput().getText();
    }

    private Prompt generateTeamPrompt(Query query) {
        List<SemanticMatch> candidateSuperheroes = getEmbeddingCandidates(query);

        String candidates = candidateSuperheroes.stream()
                .map(this::getCandidate)
                .collect(Collectors.joining());

        return promptService.generateContextualPrompt(query, candidates);
    }

    private List<SemanticMatch> getEmbeddingCandidates(Query query) {
        return teamService.retrieveEmbeddingTeam(query);
    }

    private String getCandidate(SemanticMatch semanticMatch) {
        Prompt prompt = promptService.generateSuperheroProfile(semanticMatch.superhero());
        return prompt.getContents();
    }

}
