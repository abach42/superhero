package com.abach42.superhero.skillprofile;

import static com.abach42.superhero.shared.api.PathConfig.SKILL_PROFILES;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.skill.SkillDto;
import com.abach42.superhero.testconfiguration.TestContainerConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tags(value = {@Tag("integration"), @Tag("skillprofile")})
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class SkillProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private SkillProfileDto validCreateDto;

    private SkillProfileDto validUpdateDto;

    @BeforeEach
    void setUp() {
        validCreateDto = new SkillProfileDto(null, null, 3,
                new SkillDto(3L, null)
        );

        validUpdateDto = new SkillProfileDto(
                null,
                null,
                4,
                null
        );
    }

    private URI buildUriSkillProfileList(Long superheroId) {
        return UriComponentsBuilder.fromPath(SKILL_PROFILES)
                .build(superheroId);
    }

    private URI buildUriSkillProfileSingle(Long superheroId, Long skillId) {
        return UriComponentsBuilder.fromPath(SKILL_PROFILES)
                .pathSegment("{skillId}")
                .build(superheroId, skillId);
    }

    @Nested
    @DisplayName("Test critical handling in actions")
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    class TestActionProblemHandling {

        @Test
        @DisplayName("POST /skill-profiles - Should validate missing intensity")
        void shouldValidateMissingIntensity() throws Exception {
            SkillProfileDto invalidDto = new SkillProfileDto(null, null, null,
                    new SkillDto(3L, null));
            String jsonContent = objectMapper.writeValueAsString(invalidDto);

            mockMvc.perform(post(buildUriSkillProfileList(1L))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors").exists());
        }

        @Test
        @DisplayName("POST /skill-profiles - Should validate missing skill")
        void shouldValidateMissingSkill() throws Exception {
            SkillProfileDto invalidDto = new SkillProfileDto(null, null, 3,
                    null);
            String jsonContent = objectMapper.writeValueAsString(invalidDto);

            mockMvc.perform(post(buildUriSkillProfileList(1L))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors").exists());
        }

        @Test
        @DisplayName("POST /skill-profiles - Should validate empty payload")
        void shouldValidateEmptyPayload() throws Exception {
            mockMvc.perform(post(buildUriSkillProfileList(1L))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("POST /skill-profiles - Should handle non-existent superhero")
        void shouldHandleNonExistentSuperhero() throws Exception {
            String jsonContent = objectMapper.writeValueAsString(validCreateDto);

            mockMvc.perform(post(buildUriSkillProfileList(999L))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("PATCH /skill-profiles/{skillId} - Should validate missing intensity on update")
        void shouldValidateMissingIntensityOnUpdate() throws Exception {
            SkillProfileDto invalidDto = new SkillProfileDto(null, null, null,
                    null);
            String jsonContent = objectMapper.writeValueAsString(invalidDto);

            mockMvc.perform(patch(buildUriSkillProfileSingle(1L, 2L))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent)
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors").exists());
        }

        @Test
        @DisplayName("DELETE /skill-profiles/{skillId} - Should handle non-existent skill profile")
        void shouldHandleNonExistentSkillProfileOnDelete() throws Exception {
            mockMvc.perform(delete(buildUriSkillProfileSingle(1L, 999L)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Method Security Tests")
    class MethodSecurity {

        @Test
        @DisplayName("Should allow USER to access read endpoints and deny write endpoints")
        @WithMockUser(authorities = {"ROLE_USER"})
        void shouldAllowUserReadOnlyAccess() throws Exception {
            mockMvc.perform(get(buildUriSkillProfileList(1L)))
                    .andExpect(status().isOk());

            mockMvc.perform(get(buildUriSkillProfileSingle(1L, 2L)))
                    .andExpect(status().isOk());

            String postPayload = objectMapper.writeValueAsString(validCreateDto);

            mockMvc.perform(post(buildUriSkillProfileList(1L))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(postPayload))
                    .andDo(print())
                    .andExpect(status().isForbidden());

            String putPayload = objectMapper.writeValueAsString(validUpdateDto);

            mockMvc.perform(patch(buildUriSkillProfileSingle(1L, 2L))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(putPayload))
                    .andExpect(status().isForbidden());

            mockMvc.perform(delete(buildUriSkillProfileSingle(1L, 1L)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("ADMIN should access all endpoints")
        @WithMockUser(authorities = {"ROLE_ADMIN"})
        void shouldAllowAdminFullAccess() throws Exception {
            mockMvc.perform(get(buildUriSkillProfileList(1L)))
                    .andExpect(status().isOk());

            mockMvc.perform(get(buildUriSkillProfileSingle(1L, 2L)))
                    .andExpect(status().isOk());

            String postPayload = objectMapper.writeValueAsString(validCreateDto);

            mockMvc.perform(post(buildUriSkillProfileList(1L))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(postPayload))
                    .andDo(print())
                    .andExpect(status().isCreated());

            String putPayload = objectMapper.writeValueAsString(validUpdateDto);

            mockMvc.perform(patch(buildUriSkillProfileSingle(1L, 2L))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(putPayload))
                    .andExpect(status().isOk());

            mockMvc.perform(delete(buildUriSkillProfileSingle(1L, 1L)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Not existing user should be forbidden")
        @WithAnonymousUser
        void shouldDisallowAnonymous() throws Exception {
            mockMvc.perform(get(buildUriSkillProfileList(1L)))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get(buildUriSkillProfileSingle(1L, 2L)))
                    .andExpect(status().isForbidden());

            String postPayload = objectMapper.writeValueAsString(validCreateDto);

            mockMvc.perform(post(buildUriSkillProfileList(1L))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(postPayload))
                    .andExpect(status().isForbidden());

            String putPayload = objectMapper.writeValueAsString(validUpdateDto);

            mockMvc.perform(patch(buildUriSkillProfileSingle(1L, 2L))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(putPayload))
                    .andExpect(status().isForbidden());

            mockMvc.perform(delete(buildUriSkillProfileSingle(1L, 1L)))
                    .andExpect(status().isForbidden());
        }
    }
}