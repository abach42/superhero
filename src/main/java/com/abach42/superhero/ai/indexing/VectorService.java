package com.abach42.superhero.ai.indexing;

import com.abach42.superhero.superhero.Superhero;
import com.abach42.superhero.superhero.SuperheroService;
import java.util.List;
import java.util.function.Supplier;
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

    public void updateEmbedding(Superhero hero) {
        Document doc = documentService.toDocument(hero);
        vectorStore.add(List.of(doc));
    }

    public void removeEmbedding(Superhero hero) {
        Document doc = documentService.toDocument(hero);
        vectorStore.delete(List.of(doc.getId()));
    }

    /**
     * Searching similar superheroes via embeddings
     */
    public List<Document> searchSimilarMatch(String heroDescription,
            Supplier<Integer> topKResolver) {
        try {
            return vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(heroDescription)
                            .topK(topKResolver.get())
                            .build()
            );
        } catch (Exception e) {
            throw new VectorException("Semantic search failed.");
        }
    }
}
