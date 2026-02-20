package com.abach42.superhero.ai;

import com.abach42.superhero.ai.indexing.DocumentService;
import com.abach42.superhero.ai.indexing.VectorService;
import com.abach42.superhero.shared.api.ApiException;
import com.abach42.superhero.superhero.Superhero;
import com.abach42.superhero.superhero.SuperheroService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.ai.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SimilarService {

    private final SuperheroService superheroService;
    private final DocumentService documentService;
    private final VectorService vectorService;

    public SimilarService(
            SuperheroService superheroService,
            DocumentService documentService, VectorService vectorService) {
        this.superheroService = superheroService;
        this.documentService = documentService;
        this.vectorService = vectorService;
    }

    public List<RelevantSuperheroesDto> searchSimilarHeroes(String heroDescription, int quantity) {
        try {
            var docs = vectorService.searchSimilarMatch(heroDescription, () -> quantity);
            Map<Long, Double> heroIdToScore = extractSuperheroIds(docs);

            Set<Long> heroIds = heroIdToScore.keySet();
            List<Superhero> heroes = superheroService.retrieveSuperheroesInList(heroIds);

            List<SemanticMatch> matches = documentService.generateSemanticMatches(docs, heroes);

            return matches.stream().map(RelevantSuperheroesDto::fromSemanticMatch).toList();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private Map<Long, Double> extractSuperheroIds(List<Document> docs) {
        return docs.stream()
                .collect(Collectors.toMap(
                        documentService::getSuperheroId,
                        documentService::getDistance
                ));
    }
}
