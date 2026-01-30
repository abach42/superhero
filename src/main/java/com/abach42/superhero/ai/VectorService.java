package com.abach42.superhero.ai;

import com.abach42.superhero.superhero.Superhero;
import com.abach42.superhero.superhero.SuperheroService;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VectorService {

    private final DocumentService documentService;
    private final SuperheroService superheroService;
    private final VectorStore vectorStore;

    public VectorService(DocumentService documentService, SuperheroService superheroService,
            VectorStore vectorStore) {
        this.documentService = documentService;
        this.superheroService = superheroService;
        this.vectorStore = vectorStore;
    }

    @Transactional
    public void updateSuperheroes() {
        List<Superhero> heroes = superheroService.retrieveAllSuperheroes();
        heroes.forEach(this::updateEmbedding);
    }

    private void updateEmbedding(Superhero hero) {
        Document doc = documentService.toDocument(hero);
        vectorStore.add(List.of(doc));
    }

    /**
     * Searching similar superheroes via embeddings, double up team size for more emphasize on skills.
     */
    public List<Document> searchTeamMatch(String taskDescription, int teamSize) {
        try {
            return vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(taskDescription)
                            .topK(teamSize * 2)
                            .build()
            );
        } catch (Exception e) {
            throw new VectorException("Search team failed.");
        }
    }

    public List<Document> searchSimilarMatch(String heroDescription, int quantity) {
        try {
            return vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(heroDescription)
                            .topK(quantity)
                            .build()
            );
        } catch (Exception e) {
            throw new VectorException("Search similar failed.");
        }
    }
}
