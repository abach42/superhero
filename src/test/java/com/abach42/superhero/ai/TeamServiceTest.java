package com.abach42.superhero.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
class TeamServiceTest {

    @Mock
    private SuperheroService superheroService;

    @Mock
    private DocumentService documentService;

    @Mock
    private VectorService vectorService;

    private TeamService subject;

    @BeforeEach
    void setUp() {
        subject = new TeamService(superheroService, documentService, vectorService);
    }

    @Test
    @DisplayName("Should recommend team successfully")
    void shouldRecommendTeamSuccessfully() {
        String task = "save the world";
        int teamSize = 1;
        Document doc = mock(Document.class);
        List<Document> docs = List.of(doc);
        Superhero hero = TestStubs.getSuperheroStub();
        List<Superhero> heroes = List.of(hero);
        SemanticMatch match = new SemanticMatch(SuperheroSkillDto.fromDomain(hero), 0.9);
        List<SemanticMatch> matches = new java.util.ArrayList<>(List.of(match));

        given(vectorService.searchSimilarMatch(any(), any())).willReturn(docs);
        given(documentService.getSuperheroId(doc)).willReturn(1L);
        given(superheroService.retrieveSuperheroesInList(any())).willReturn(heroes);
        given(documentService.generateSemanticMatches(docs, heroes)).willReturn(matches);

        SuperheroTeam result = subject.recommendTeam(task, teamSize);

        assertThat(result.taskDescription()).isEqualTo(task);
        assertThat(result.members()).hasSize(1);
        assertThat(result.members().get(0).similarity()).isEqualTo(0.9);
        verify(vectorService).searchSimilarMatch(any(), any());
    }

    @Test
    @DisplayName("Should throw SemanticSearchException when sorting fails")
    void shouldThrowSemanticSearchExceptionWhenSortingFails() {
        String task = "save the world";
        int teamSize = 1;
        Document doc = mock(Document.class);
        List<Document> docs = List.of(doc);
        Superhero hero = TestStubs.getSuperheroStub();
        List<Superhero> heroes = List.of(hero);
        
        // SemanticMatch that will throw exception on similarity()
        SemanticMatch match = mock(SemanticMatch.class);
        // TeamService.sortSemantic uses matches.sort(Comparator.comparingDouble(SemanticMatch::similarity).reversed());
        // reversed() might call similarity() multiple times or during comparison.
        given(match.similarity()).willThrow(new RuntimeException("Similarity failed"));
        
        List<SemanticMatch> matches = new java.util.ArrayList<>(List.of(match, match)); // Need at least 2 to trigger comparison usually, but sort might call it even for 1 in some implementations, though unlikely to throw if not compared. 
        // Actually Comparator.comparingDouble calls the keyExtractor.

        given(vectorService.searchSimilarMatch(any(), any())).willReturn(docs);
        given(documentService.getSuperheroId(doc)).willReturn(1L);
        given(superheroService.retrieveSuperheroesInList(any())).willReturn(heroes);
        given(documentService.generateSemanticMatches(docs, heroes)).willReturn(matches);

        assertThatThrownBy(() -> subject.recommendTeam(task, teamSize))
                .isInstanceOf(com.abach42.superhero.shared.api.ApiException.class)
                .hasMessageContaining("Sorting failed");
    }
}
