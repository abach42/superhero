package com.abach42.superhero.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.abach42.superhero.ai.indexing.DocumentService;
import com.abach42.superhero.ai.indexing.VectorException;
import com.abach42.superhero.ai.indexing.VectorService;
import com.abach42.superhero.superhero.Superhero;
import com.abach42.superhero.superhero.SuperheroService;
import com.abach42.superhero.testconfiguration.TestStubs;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith(MockitoExtension.class)
class VectorServiceTest {

    @Mock
    private DocumentService documentService;

    @Mock
    private SuperheroService superheroService;

    @Mock
    private VectorStore vectorStore;

    private VectorService subject;

    @BeforeEach
    void setUp() {
        subject = new VectorService(documentService, superheroService, vectorStore);
    }

    @Test
    @DisplayName("Should update all superheroes embeddings")
    void shouldUpdateSuperheroes() {
        Superhero hero = TestStubs.getSuperheroStub();
        given(superheroService.retrieveAllSuperheroes()).willReturn(List.of(hero));
        Document doc = new Document("1", "content", Map.of());
        given(documentService.toDocument(hero)).willReturn(doc);

        subject.updateSuperheroes();

        verify(superheroService).retrieveAllSuperheroes();
        verify(vectorStore).add(any());
    }

    @Test
    @DisplayName("Should update embedding for a hero")
    void shouldUpdateEmbedding() {
        Superhero hero = TestStubs.getSuperheroStub();
        Document doc = new Document("1", "content", Map.of());
        given(documentService.toDocument(hero)).willReturn(doc);

        subject.updateEmbedding(hero);

        verify(vectorStore).add(any());
    }

    @Test
    @DisplayName("Should remove embedding for a hero")
    void shouldRemoveEmbedding() {
        Superhero hero = TestStubs.getSuperheroStub();
        Document doc = new Document("1", "content", Map.of());
        given(documentService.toDocument(hero)).willReturn(doc);

        subject.removeEmbedding(hero);

        verify(vectorStore).delete(List.of(doc.getId()));
    }

    @Test
    @DisplayName("Should search similar match successfully")
    void shouldSearchSimilarMatchSuccessfully() {
        String description = "search query";
        Document doc = new Document("1", "content", Map.of());
        given(vectorStore.similaritySearch(any(SearchRequest.class))).willReturn(List.of(doc));

        List<Document> result = subject.searchSimilarMatch(description, () -> 5);

        assertThat(result).hasSize(1);
        verify(vectorStore).similaritySearch(any(SearchRequest.class));
    }

    @Test
    @DisplayName("Should throw VectorException when search fails")
    void shouldThrowVectorExceptionWhenSearchFails() {
        given(vectorStore.similaritySearch(any(SearchRequest.class))).willThrow(
                new RuntimeException("Search failed"));

        assertThatThrownBy(() -> subject.searchSimilarMatch("query", () -> 5))
                .isInstanceOf(VectorException.class)
                .hasMessage("Search similar failed.");
    }
}
