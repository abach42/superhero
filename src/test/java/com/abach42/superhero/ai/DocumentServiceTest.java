package com.abach42.superhero.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.abach42.superhero.superhero.Superhero;
import com.abach42.superhero.testconfiguration.TestStubs;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private ContentService contentService;

    @InjectMocks
    private DocumentService subject;

    @Test
    @DisplayName("Should convert superhero to document")
    void shouldConvertSuperheroToDocument() {
        Superhero hero = TestStubs.getSuperheroStub();

        Document result = subject.toDocument(hero);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getMetadata()).containsEntry("superheroId", hero.getId());
        assertThat(result.getMetadata()).containsEntry("alias", hero.getAlias());
    }

    @Test
    @DisplayName("Should get superhero id from document")
    void shouldGetSuperheroIdFromDocument() {
        Document doc = new Document("1", "content", Map.of("superheroId", 42L));

        Long result = subject.getSuperheroId(doc);

        assertThat(result).isEqualTo(42L);
    }

    @Test
    @DisplayName("Should get distance from document")
    void shouldGetDistanceFromDocument() {
        // Arrange
        Document docWithScore = org.mockito.Mockito.mock(Document.class);
        org.mockito.Mockito.when(docWithScore.getScore()).thenReturn(0.85);

        Document docWithoutScore = org.mockito.Mockito.mock(Document.class);
        org.mockito.Mockito.when(docWithoutScore.getScore()).thenReturn(null);

        // Act
        Double score = subject.getDistance(docWithScore);
        Double defaultScore = subject.getDistance(docWithoutScore);

        // Assert
        assertThat(score).isEqualTo(0.85);
        assertThat(defaultScore).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should generate semantic matches with correct scores")
    void shouldGenerateSemanticMatchesWithCorrectScores() {
        // Arrange
        Document doc = org.mockito.Mockito.mock(Document.class);
        org.mockito.Mockito.when(doc.getScore()).thenReturn(0.75);
        org.mockito.Mockito.when(doc.getMetadata()).thenReturn(Map.of("superheroId", 0L));
        
        List<Document> docs = List.of(doc);
        Superhero hero = TestStubs.getSuperheroStub();
        List<Superhero> heroes = List.of(hero);

        // Act
        List<SemanticMatch> result = subject.generateSemanticMatches(docs, heroes);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).similarity()).isEqualTo(0.75);
        assertThat(result.get(0).superhero().alias()).isEqualTo(hero.getAlias());
    }
}
