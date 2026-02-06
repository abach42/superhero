package com.abach42.superhero.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.shared.api.PathConfig;
import com.abach42.superhero.testconfiguration.ObjectMapperSerializerHelper;
import com.abach42.superhero.testconfiguration.TestContainerConfiguration;
import com.abach42.superhero.testconfiguration.TestStubs;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

@Tags(value = {@Tag("integration"), @Tag("ai")})
@SpringBootTest(classes = {TestContainerConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@Import({ObjectMapperSerializerHelper.class})
public class SimilarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SimilarService similarService;

    @Test
    @DisplayName("Should search similar superheroes successfully")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void shouldSearchSimilarSuperheroesSuccessfully() throws Exception {
        String query = "Batman";
        int quantity = 5;

        SemanticMatch match = new SemanticMatch(SuperheroSkillDto.fromDomain(
                TestStubs.getSuperheroStub()), 0.95);
        given(similarService.searchSimilarHeroes(anyString(), anyInt())).willReturn(List.of(match));

        String uri = UriComponentsBuilder.fromPath(PathConfig.SUPERHEROES)
                .pathSegment("search")
                .queryParam("query", query)
                .queryParam("quantity", String.valueOf(quantity))
                .toUriString();

        MvcResult result = mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<SemanticMatch> response = objectMapper.readValue(content, 
                objectMapper.getTypeFactory().constructCollectionType(List.class,
                        SemanticMatch.class));
        
        assertThat(response).hasSize(1);
        assertThat(response.get(0).similarity()).isEqualTo(0.95);
    }
}
