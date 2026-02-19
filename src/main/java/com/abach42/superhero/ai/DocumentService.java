package com.abach42.superhero.ai;

import com.abach42.superhero.superhero.Superhero;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private final ContentService contentService;

    public DocumentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public Double getDistance(Document doc) {
        if (doc.getScore() == null) {
            return 0.0;
        }

        return doc.getScore();
    }

    public Long getSuperheroId(Document doc) {
        return ((Number) doc.getMetadata().get("superheroId")).longValue();
    }

    /**
     * Generate semantic matches sorted by score, put to mutable list
     */
    public List<SemanticMatch> generateSemanticMatches(List<Document> docs,
            List<Superhero> heroes) {
        return docs.stream().map(doc -> {
            long heroId = getSuperheroId(doc);

            Superhero hero = heroes.stream()
                    .filter(h -> h.getId() == heroId)
                    .findFirst()
                    .orElseThrow();

            double score = getDistance(doc);
            return new SemanticMatch(SuperheroSkillDto.fromDomain(hero), score);
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Converts a Superhero into a Spring AI Document that will be
     * embedded and stored in pgvector.
     */
    public Document toDocument(Superhero hero) {

        String content = buildContent(hero);
        Map<String, Object> metadata = buildMetadata(hero);

        String consistentId = UUID.nameUUIDFromBytes(("superhero:" + hero.getId()).getBytes())
                .toString();

        return new Document(consistentId, content, metadata);
    }

    /**
     * The semantic text that will be embedded.
     * This is where "meaning" lives.
     */
    private String buildContent(Superhero hero) {
        return contentService.getContent(AllContentStrategy.QUALIFIER, hero);
    }

    /**
     * Structured data that is NOT embedded but used for filtering,
     * lookups and joins back to JPA.
     */
    private Map<String, Object> buildMetadata(Superhero hero) {
        Map<String, Object> meta = new HashMap<>();

        // Required for updates
        meta.put("id", "superhero:" + hero.getId());

        // Used to re-load the entity
        meta.put("superheroId", hero.getId());

        // Useful for debugging & UI
        meta.put("alias", hero.getAlias());
        meta.put("realName", hero.getRealName());
        meta.put("occupation", hero.getOccupation());
        meta.put("gender", hero.getGender());

        return meta;
    }
}
