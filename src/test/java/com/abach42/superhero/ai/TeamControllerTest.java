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
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TeamService teamService;

    @Test
    @DisplayName("Should recommend team successfully")
    @WithMockUser(authorities = "ROLE_ADMIN")
    void shouldRecommendTeamSuccessfully() throws Exception {
        String task = "Rescue mission";
        int teamSize = 3;

        SemanticMatch match = new SemanticMatch(SuperheroSkillDto.fromDomain(
                TestStubs.getSuperheroStub()), 0.88);
        SuperheroTeam team = new SuperheroTeam(task, List.of(match));
        given(teamService.recommendTeam(anyString(), anyInt())).willReturn(team);

        String uri = UriComponentsBuilder.fromPath(PathConfig.SUPERHEROES)
                .pathSegment("team")
                .queryParam("task", task)
                .queryParam("teamSize", String.valueOf(teamSize))
                .toUriString();

        MvcResult result = mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        SuperheroTeam response = objectMapper.readValue(content, SuperheroTeam.class);

        assertThat(response.taskDescription()).isEqualTo(task);
        assertThat(response.members()).hasSize(1);
    }
}
