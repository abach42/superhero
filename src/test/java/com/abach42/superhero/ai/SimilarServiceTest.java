package com.abach42.superhero.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.abach42.superhero.ai.indexing.DocumentService;
import com.abach42.superhero.ai.indexing.VectorService;
import com.abach42.superhero.superhero.Superhero;
import com.abach42.superhero.superhero.SuperheroService;
import com.abach42.superhero.testconfiguration.TestStubs;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;

@ExtendWith(MockitoExtension.class)
class SimilarServiceTest {

    @Mock
    private SuperheroService superheroService;

    @Mock
    private DocumentService documentService;

    @Mock
    private VectorService vectorService;

    private SimilarService subject;

    @BeforeEach
    void setUp() {
        subject = new SimilarService(superheroService, documentService, vectorService);
    }

    @Test
    @DisplayName("Should search similar heroes successfully")
    void shouldSearchSimilarHeroesSuccessfully() {
        String query = "strong hero";
        int quantity = 5;
        Document doc = mock(Document.class);
        List<Document> docs = List.of(doc);
        Superhero hero = TestStubs.getSuperheroStub();
        List<Superhero> heroes = List.of(hero);
        SemanticMatch match = new SemanticMatch(hero, 0.9);

        given(vectorService.searchSimilarMatch(any())).willReturn(docs);
        given(documentService.getSuperheroId(doc)).willReturn(1L);
        given(documentService.getDistance(doc)).willReturn(0.9);
        given(superheroService.retrieveSuperheroesInList(Set.of(1L))).willReturn(heroes);
        given(documentService.generateSemanticMatches(docs, heroes)).willReturn(List.of(match));

        List<RelevantSuperheroesDto> result = subject.searchSimilarHeroes(query, quantity);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).relevance()).isEqualTo(0.9);
        verify(vectorService).searchSimilarMatch(any());
    }
}
