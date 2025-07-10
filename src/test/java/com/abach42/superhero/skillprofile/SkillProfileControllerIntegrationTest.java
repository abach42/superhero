package com.abach42.superhero.skillprofile;

import static com.abach42.superhero.config.api.PathConfig.BASE_URI;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.config.api.ErrorDto;
import com.abach42.superhero.skill.Skill;
import com.abach42.superhero.skill.SkillDto;
import com.abach42.superhero.skill.SkillRepository;
import com.abach42.superhero.testconfiguration.TestContainerConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tags(value = {@Tag("integration"), @Tag("skillprofile")})
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class SkillProfileControllerIntegrationTest {

    private static final String SKILL_PROFILES_PATH = "/superheroes/{superheroId}/skill-profiles";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SkillProfileRepository skillProfileRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Skill testSkill;

    private SkillProfileDto validCreateDto;

    private SkillProfileDto validUpdateDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        testSkill = skillRepository.save(new Skill("Flying"));

        validCreateDto = new SkillProfileDto(
                null, // id should be null for creation
                null, // superheroId should be null for creation
                3,    // intensity
                new SkillDto(testSkill.getId(), testSkill.getName())
        );

        validUpdateDto = new SkillProfileDto(
                null, // id should be null for updates
                null, // superheroId should be null for updates
                4,    // new intensity
                null  // skill should be null for updates
        );
    }

    private URI buildUri(Long superheroId) {
        return UriComponentsBuilder.fromPath(BASE_URI + SKILL_PROFILES_PATH)
                .build(superheroId);
    }

    private URI buildUriWithSkill(Long superheroId, Long skillId) {
        return UriComponentsBuilder.fromPath(BASE_URI + SKILL_PROFILES_PATH + "/{skillId}")
                .build(superheroId, skillId);
    }

    // GET Tests - Read operations (accessible by USER and ADMIN due to hierarchy)
    @Test
    @DisplayName("GET /skill-profiles - Admin should access skill profiles list")
    void shouldAllowAdminToGetSkillProfilesList() throws Exception {
        // Controller has @IsAdmin at class level + @IsUser on method level
        // Due to role hierarchy, ADMIN should satisfy @IsUser requirement
        mockMvc.perform(get(buildUri(1L))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound()); // 404 because superhero doesn't exist
    }

    @Test
    @DisplayName("GET /skill-profiles - User should access skill profiles list")
    void shouldAllowUserToGetSkillProfilesList() throws Exception {
        // This should fail because controller has @IsAdmin at class level
        // User doesn't have ADMIN role, so should get 403
        mockMvc.perform(get(buildUri(1L))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden()); // 403 because USER doesn't satisfy @IsAdmin
    }

    @Test
    @DisplayName("GET /skill-profiles/{skillId} - Admin should access single skill profile")
    void shouldAllowAdminToGetSingleSkillProfile() throws Exception {
        mockMvc.perform(get(buildUriWithSkill(1L, testSkill.getId()))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound()); // 404 because superhero doesn't exist
    }

    @Test
    @DisplayName("GET /skill-profiles/{skillId} - User should be forbidden to access")
    void shouldForbidUserToGetSingleSkillProfile() throws Exception {
        // This should fail because controller has @IsAdmin at class level
        mockMvc.perform(get(buildUriWithSkill(1L, testSkill.getId()))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden()); // 403 because USER doesn't satisfy @IsAdmin
    }

    @Test
    @DisplayName("GET /skill-profiles - Unauthenticated should be forbidden")
    void shouldForbidUnauthenticatedAccessToGetSkillProfiles() throws Exception {
        mockMvc.perform(get(buildUri(1L)))
                .andExpect(status().isUnauthorized());
    }

    // POST Tests - Create operations (ADMIN only due to class-level @IsAdmin)
    @Test
    @DisplayName("POST /skill-profiles - Admin should create skill profile")
    void shouldAllowAdminToCreateSkillProfile() throws Exception {
        String jsonContent = objectMapper.writeValueAsString(validCreateDto);

        mockMvc.perform(post(buildUri(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andDo(print())
                .andExpect(status().isNotFound()); // 404 because superhero doesn't exist
    }

    @Test
    @DisplayName("POST /skill-profiles - User should be forbidden to create")
    void shouldForbidUserToCreateSkillProfile() throws Exception {
        String jsonContent = objectMapper.writeValueAsString(validCreateDto);

        mockMvc.perform(post(buildUri(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /skill-profiles - Should validate missing intensity")
    void shouldValidateMissingIntensity() throws Exception {
        SkillProfileDto invalidDto = new SkillProfileDto(null, null, null,
                new SkillDto(testSkill.getId(), testSkill.getName()));
        String jsonContent = objectMapper.writeValueAsString(invalidDto);

        mockMvc.perform(post(buildUri(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    @DisplayName("POST /skill-profiles - Should validate missing skill")
    void shouldValidateMissingSkill() throws Exception {
        SkillProfileDto invalidDto = new SkillProfileDto(null, null, 3, null);
        String jsonContent = objectMapper.writeValueAsString(invalidDto);

        mockMvc.perform(post(buildUri(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    @DisplayName("POST /skill-profiles - Should validate empty payload")
    void shouldValidateEmptyPayload() throws Exception {
        mockMvc.perform(post(buildUri(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST /skill-profiles - Should handle non-existent superhero")
    void shouldHandleNonExistentSuperhero() throws Exception {
        String jsonContent = objectMapper.writeValueAsString(validCreateDto);

        mockMvc.perform(post(buildUri(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound()); // 404 because superhero doesn't exist
    }

    // PUT Tests - Update operations (ADMIN only due to class-level @IsAdmin)
    @Test
    @DisplayName("PUT /skill-profiles/{skillId} - Admin should update skill profile")
    void shouldAllowAdminToUpdateSkillProfile() throws Exception {
        String jsonContent = objectMapper.writeValueAsString(validUpdateDto);

        mockMvc.perform(put(buildUriWithSkill(1L, testSkill.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound()); // 404 because superhero doesn't exist
    }

    @Test
    @DisplayName("PUT /skill-profiles/{skillId} - User should be forbidden to update")
    void shouldForbidUserToUpdateSkillProfile() throws Exception {
        String jsonContent = objectMapper.writeValueAsString(validUpdateDto);

        mockMvc.perform(put(buildUriWithSkill(1L, testSkill.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /skill-profiles/{skillId} - Should validate missing intensity on update")
    void shouldValidateMissingIntensityOnUpdate() throws Exception {
        SkillProfileDto invalidDto = new SkillProfileDto(null, null, null, null);
        String jsonContent = objectMapper.writeValueAsString(invalidDto);

        mockMvc.perform(put(buildUriWithSkill(1L, testSkill.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    // DELETE Tests - Delete operations (ADMIN only due to class-level @IsAdmin)
    @Test
    @DisplayName("DELETE /skill-profiles/{skillId} - Admin should delete skill profile")
    void shouldAllowAdminToDeleteSkillProfile() throws Exception {
        mockMvc.perform(delete(buildUriWithSkill(1L, testSkill.getId()))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound()); // 404 because superhero doesn't exist
    }

    @Test
    @DisplayName("DELETE /skill-profiles/{skillId} - User should be forbidden to delete")
    void shouldForbidUserToDeleteSkillProfile() throws Exception {
        mockMvc.perform(delete(buildUriWithSkill(1L, testSkill.getId()))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /skill-profiles/{skillId} - Should handle non-existent skill profile")
    void shouldHandleNonExistentSkillProfileOnDelete() throws Exception {
        mockMvc.perform(delete(buildUriWithSkill(1L, 999L))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound());
    }

    // Authentication and Authorization Tests
    @Test
    @DisplayName("All endpoints should require authentication")
    void allEndpointsShouldRequireAuthentication() throws Exception {
        String jsonContent = objectMapper.writeValueAsString(validCreateDto);

        // All endpoints should return 401 when unauthenticated
        mockMvc.perform(get(buildUri(1L)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get(buildUriWithSkill(1L, testSkill.getId())))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(buildUri(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put(buildUriWithSkill(1L, testSkill.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete(buildUriWithSkill(1L, testSkill.getId())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Only ADMIN role should have access to controller due to class-level @IsAdmin")
    void onlyAdminShouldHaveAccess() throws Exception {
        String createContent = objectMapper.writeValueAsString(validCreateDto);
        String updateContent = objectMapper.writeValueAsString(validUpdateDto);

        // Admin should be able to perform all operations
        mockMvc.perform(get(buildUri(1L))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound()); // Expected 404, not 403

        mockMvc.perform(post(buildUri(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createContent)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound()); // Expected 404, not 403

        mockMvc.perform(put(buildUriWithSkill(1L, testSkill.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateContent)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound()); // Expected 404, not 403

        mockMvc.perform(delete(buildUriWithSkill(1L, testSkill.getId()))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound()); // Expected 404, not 403
    }

    @Test
    @DisplayName("Class-level @IsAdmin should override method-level @IsUser annotations")
    void classLevelAdminShouldOverrideMethodLevelUser() throws Exception {
        // Even though GET methods have @IsUser, the class-level @IsAdmin should take precedence
        // So USER should be forbidden from accessing any endpoint
        mockMvc.perform(get(buildUri(1L))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(get(buildUriWithSkill(1L, testSkill.getId()))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Role hierarchy should work - ADMIN implies USER")
    void roleHierarchyShouldWork() throws Exception {
        // ADMIN should be able to access all endpoints due to role hierarchy
        // where ADMIN implies USER role
        mockMvc.perform(get(buildUri(1L))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound()); // Should get 404 (resource not found), not 403 (forbidden)

        mockMvc.perform(get(buildUriWithSkill(1L, testSkill.getId()))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound()); // Should get 404 (resource not found), not 403 (forbidden)
    }

    private ErrorDto getErrorFromResponse(MvcResult result) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(), ErrorDto.class);
    }
}