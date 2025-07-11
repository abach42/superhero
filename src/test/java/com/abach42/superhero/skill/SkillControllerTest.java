package com.abach42.superhero.skill;

import static com.abach42.superhero.config.api.PathConfig.SKILLS;
import static com.abach42.superhero.config.api.PathConfig.SUPERHEROES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.config.api.PathConfig;
import com.abach42.superhero.testconfiguration.TestStubs;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

@WebMvcTest(SkillController.class)
@ContextConfiguration
@WebAppConfiguration
@WithMockUser
public class SkillControllerTest {
    
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SkillService skillService;

    @Autowired
    private ObjectMapper objectMapper;
    

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @ParameterizedTest
    @MethodSource("endpointProvider")
    @DisplayName("fails w/o JWT")
    @WithAnonymousUser
    public void testAnonymousFails(HttpMethod method, String path, ResultMatcher status)
            throws Exception {
        mockMvc.perform(
                        request(method, path)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status);
    }

    private static Stream<Arguments> endpointProvider() {
        return Stream.of(
                Arguments.of(HttpMethod.GET, UriComponentsBuilder.fromPath(SKILLS).toUriString(),
                        MockMvcResultMatchers.status().isUnauthorized()),
                Arguments.of(HttpMethod.GET, UriComponentsBuilder.fromPath(SKILLS)
                                .pathSegment("{id}").buildAndExpand(0L)
                                .toUriString(),
                        MockMvcResultMatchers.status().isUnauthorized())
        );
    }

    @Test
    @DisplayName("GET /skills list skills")
    public void testListSkills() throws Exception {
        SkillListDto expected = new SkillListDto(List.of(TestStubs.getSkillDtoStub()));

        given(skillService.getSkillList()).willReturn(expected);

        String uri = UriComponentsBuilder.fromPath(SKILLS).toUriString();
        MvcResult mvcResult = mockMvc.perform(
                        get(uri)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SkillListDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SkillListDto.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /skills/0" + " returns a superhero")
    public void testShowSuperhero() throws Exception {
        SkillDto expected = TestStubs.getSkillDtoStub();

        given(skillService.getSkillConverted(anyLong())).willReturn(expected);

        String uri = UriComponentsBuilder.fromPath(SKILLS)
                .pathSegment("{id}").buildAndExpand(0L)
                .toUriString();
        MvcResult mvcResult = mockMvc.perform(get(uri).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SkillDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SkillDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
