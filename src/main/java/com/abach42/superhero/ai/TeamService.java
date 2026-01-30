package com.abach42.superhero.ai;

import com.abach42.superhero.config.api.ApiException;
import com.abach42.superhero.superhero.Superhero;
import com.abach42.superhero.superhero.SuperheroService;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.ai.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    private final SuperheroService superheroService;
    private final DocumentService documentService;
    private final VectorService vectorService;

    public TeamService(SuperheroService superheroService,
            DocumentService documentService, VectorService vectorService) {
        this.superheroService = superheroService;
        this.documentService = documentService;
        this.vectorService = vectorService;
    }

    public SuperheroTeam recommendTeam(String taskDescription, int teamSize) {
        try {
            var docs = vectorService.searchTeamMatch(taskDescription, teamSize);
            Set<Long> heroIds = extractSuperheroIds(docs);

            List<Superhero> heroes = superheroService.retrieveSuperheroesInList(heroIds);

            List<SemanticMatch> matches =
                    documentService.generateSemanticMatches(docs, heroes);

            sortSemantic(matches);

            List<SemanticMatch> teamMembers = limitToTeamSize(teamSize,
                    matches);

            return new SuperheroTeam(taskDescription, teamMembers);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private Set<Long> extractSuperheroIds(List<Document> docs) {
        return docs.stream()
                .map(documentService::getSuperheroId)
                .collect(Collectors.toSet());
    }

    /**
     * Sort all candidates by semantic similarity of skills, descending.
     */
    private static void sortSemantic(List<SemanticMatch> matches) {
        try {
            matches.sort(Comparator.comparingDouble(SemanticMatch::similarity).reversed());
        } catch (Exception e) {
            throw new SemanticSearchException("Sorting failed " + e.getMessage(), e);
        }
    }

    private static List<SemanticMatch> limitToTeamSize(int teamSize,
            List<SemanticMatch> matches) {
        return matches.stream().limit(teamSize).toList();
    }


}
