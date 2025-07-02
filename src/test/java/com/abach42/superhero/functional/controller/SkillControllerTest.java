package com.abach42.superhero.functional.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.config.api.PathConfig;
import com.abach42.superhero.configuration.TestDataConfiguration;
import com.abach42.superhero.controller.SkillController;
import com.abach42.superhero.dto.SkillDto;
import com.abach42.superhero.dto.SkillListDto;
import com.abach42.superhero.service.SkillService;
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

/*
 * Mocked rest client, regarding validation, mocked database
 *
 * * Fails no auth
 * * Read succeeds
 */
@WebMvcTest(SkillController.class)
@ContextConfiguration
@WebAppConfiguration
@WithMockUser
public class SkillControllerTest {

    private final static String PATH = PathConfig.SKILLS;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SkillService skillService;

    @Autowired
    private ObjectMapper objectMapper;

    private static Stream<Arguments> endpointProvider() {
        return Stream.of(
                Arguments.of(HttpMethod.GET, PATH, MockMvcResultMatchers.status().isUnauthorized()),
                Arguments.of(HttpMethod.GET, PATH + "/" + 0,
                        MockMvcResultMatchers.status().isUnauthorized())
        );
    }

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

    @Test
    @DisplayName("GET " + PATH + " list skills")
    public void testListSkills() throws Exception {
        SkillListDto expected = new SkillListDto(List.of(TestDataConfiguration.getSkillDtoStub()));

        given(skillService.getSkillList()).willReturn(expected);

        MvcResult mvcResult = mockMvc.perform(
                        get(PATH)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SkillListDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SkillListDto.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("GET " + PATH + "/0" + " returns a superhero")
    public void testShowSuperhero() throws Exception {
        SkillDto expected = TestDataConfiguration.getSkillDtoStub();

        given(skillService.getSkillConverted(anyLong())).willReturn(expected);

        MvcResult mvcResult = mockMvc.perform(
                        get(PATH + "/" + 0)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SkillDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SkillDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
