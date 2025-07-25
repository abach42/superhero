package com.abach42.superhero.skillprofile;

import static com.abach42.superhero.config.api.PathConfig.SKILL_PROFILES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.config.validation.OnCreate;
import com.abach42.superhero.testconfiguration.ObjectMapperSerializerHelper;
import com.abach42.superhero.testconfiguration.TestStubs;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

@WebMvcTest(SkillProfileController.class)
@ContextConfiguration
@WebAppConfiguration
@Import(ObjectMapperSerializerHelper.class)
@WithMockUser
public class SkillProfileControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SkillProfileService skillProfileService;

    private SkillProfileDto skillProfileDtoStub;

    private static URI buildUriSkillProfileList() {
        return UriComponentsBuilder.fromPath(SKILL_PROFILES)
                .build(1L);
    }

    private static URI buildUriSkillProfileMockSingle() {
        return UriComponentsBuilder.fromPath(SKILL_PROFILES)
                .pathSegment("{skillId}")
                .build(1L, 0L);
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        this.skillProfileDtoStub = TestStubs.getSkillProfileDtoStub();
    }

    @Test
    @DisplayName("GET /skill-profiles returns skill profiles of superhero")
    public void testListSuperheroSkillProfiles() throws Exception {
        SkillProfileListDto expected = new SkillProfileListDto(List.of(skillProfileDtoStub));

        given(skillProfileService.retrieveSuperheroSkillProfileList(anyLong())).willReturn(
                expected);

        MvcResult mvcResult = mockMvc.perform(
                        get(buildUriSkillProfileList())
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SkillProfileListDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SkillProfileListDto.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /skill-profiles/0" + " returns a skill profile of a superhero")
    public void testShowSuperheroSkillProfile() throws Exception {
        SkillProfileDto expected = skillProfileDtoStub;

        given(skillProfileService.retrieveSuperheroSkillProfile(anyLong(), anyLong())).willReturn(
                expected);

        MvcResult mvcResult = mockMvc.perform(
                        get(buildUriSkillProfileMockSingle())
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SkillProfileDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SkillProfileDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /skill-profiles results created")
    public void testCreateSuperheroSkillProfile() throws Exception {
        SkillProfileDto expected = SkillProfileDto.fromDomain(
                TestStubs.getSkillProfileToCreateStub());

        given(skillProfileService.addSuperheroSkillProfile(anyLong(),
                any(SkillProfileDto.class))).willReturn(expected);

        MvcResult mvcResult = mockMvc.perform(
                        post(buildUriSkillProfileList())
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(objectMapper.writeValueAsString(expected))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        SkillProfileDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SkillProfileDto.class);

        String locationHeader = mvcResult.getResponse().getHeader("Location");
        assertThat(locationHeader).isNotNull();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /skill-profiles returns 422 on missing field in payload")
    @Validated(OnCreate.class)
    public void testCreatedSuperheroSkillProfileFailsOnMissingField() throws Exception {
        SkillProfile skillProfile = TestStubs.getSkillProfileToCreateStub();
        skillProfile.setIntensity(null);

        SkillProfileDto failedSkillProfileDto = SkillProfileDto.fromDomain(skillProfile);

        mockMvc.perform(
                        post(buildUriSkillProfileList())
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(objectMapper.writeValueAsString(failedSkillProfileDto))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("PATCH /skill-profiles/" + 0 + " update updates intensity ")
    public void testUpdateSuperheroSkillProfile() throws Exception {
        SkillProfileDto expected = TestStubs.getSkillProfileDtoToUpdateStub();
        given(skillProfileService.changeSuperheroSkillProfile(anyLong(), anyLong(),
                any(SkillProfileDto.class)))
                .willReturn(expected);

        MvcResult mvcResult = mockMvc.perform(
                        patch(buildUriSkillProfileMockSingle())
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(objectMapper.writeValueAsString(expected))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SkillProfileDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SkillProfileDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Controller action to delete superhero result ok")
    public void testDeleteSuperheroSkillProfile() throws Exception {
        given(skillProfileService.deleteSuperheroSkillProfile(anyLong(), anyLong())).willReturn(
                skillProfileDtoStub);

        MvcResult mvcResult = mockMvc.perform(
                        delete(buildUriSkillProfileMockSingle())
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(objectMapper.writeValueAsString(skillProfileDtoStub))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SkillProfileDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SkillProfileDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(skillProfileDtoStub);
    }

    @ParameterizedTest
    @MethodSource("endpointProvider")
    @DisplayName("fails w/o JWT")
    @WithAnonymousUser
    public void testAnonymousFails(HttpMethod method, String path, ResultMatcher status)
            throws Exception {
        mockMvc.perform(request(method, path)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status);
    }

    private static Stream<Arguments> endpointProvider() {
        return Stream.of(
                Arguments.of(HttpMethod.GET, buildUriSkillProfileList().toString(),
                        MockMvcResultMatchers.status().isUnauthorized()),
                Arguments.of(HttpMethod.GET, buildUriSkillProfileMockSingle().toString(),
                        MockMvcResultMatchers.status().isUnauthorized()),
                Arguments.of(HttpMethod.POST, buildUriSkillProfileList().toString(),
                        MockMvcResultMatchers.status().isForbidden()),
                Arguments.of(HttpMethod.PUT, buildUriSkillProfileMockSingle().toString(),
                        MockMvcResultMatchers.status().isForbidden()),
                Arguments.of(HttpMethod.DELETE, buildUriSkillProfileMockSingle().toString(),
                        MockMvcResultMatchers.status().isForbidden())
        );
    }

}
