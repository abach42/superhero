package com.abach42.superhero.ai;

import com.abach42.superhero.ai.indexing.DocumentService;
import com.abach42.superhero.ai.indexing.VectorService;
import com.abach42.superhero.shared.api.ApiException;
import com.abach42.superhero.superhero.Superhero;
import com.abach42.superhero.superhero.SuperheroService;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
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

    /**
     * Sort all candidates by semantic relevance of skills, descending.
     */
    private static List<SemanticMatch> sortSemantic(List<SemanticMatch> matches) {
        try {
            matches.sort(Comparator.comparingDouble(SemanticMatch::relevance).reversed());
            return matches;
        } catch (Exception e) {
            throw new SemanticSearchException("Sorting failed " + e.getMessage(), e);
        }
    }

    private static List<SemanticMatch> limitToTeamSize(int teamSize,
            List<SemanticMatch> matches) {
        return matches.stream().limit(teamSize).toList();
    }

    public SuperheroEmbeddedTeamDto recommendTeam(String taskDescription, int teamSize) {
        try {
            //double up team size for more emphasize on skills.
            List<SemanticMatch> matches = retrieveEmbeddingTeam(taskDescription, () -> teamSize * 2);
            matches = limitToTeamSize(teamSize, matches);

            return SuperheroEmbeddedTeamDto.fromSemanticMatches(taskDescription, matches);
        } catch (SemanticSearchException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public List<SemanticMatch> retrieveEmbeddingTeam(String taskDescription, Supplier<Integer> teamSize)
            throws SemanticSearchException{
        try {
            var docs = vectorService.searchSimilarMatch(taskDescription, teamSize);
            Set<Long> heroIds = extractSuperheroIds(docs);

            List<Superhero> heroes = superheroService.retrieveSuperheroesInList(heroIds);

            List<SemanticMatch> matches =
                    documentService.generateSemanticMatches(docs, heroes);

            return sortSemantic(matches);
        } catch (Exception e) {
            throw new SemanticSearchException(e.getMessage(), e);
        }
    }

    private Set<Long> extractSuperheroIds(List<Document> docs) {
        return docs.stream()
                .map(documentService::getSuperheroId)
                .collect(Collectors.toSet());
    }
}
