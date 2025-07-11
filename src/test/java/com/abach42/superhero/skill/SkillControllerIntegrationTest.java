package com.abach42.superhero.skill;

import static com.abach42.superhero.config.api.PathConfig.SKILLS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.config.api.ErrorDto;
import com.abach42.superhero.testconfiguration.TestContainerConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tags(value = {@Tag("integration"), @Tag("skill")})
@SpringBootTest(classes = {TestContainerConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SkillControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private URI buildUriSkillList() {
        return UriComponentsBuilder.fromPath(SKILLS).build().toUri();
    }

    private URI buildUriSkillSingle(Long skillId) {
        return UriComponentsBuilder.fromPath(SKILLS)
                .pathSegment("{skillId}")
                .build(skillId);
    }

    @Nested
    @DisplayName("Test actions")
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    class TestActions {

        @Test
        @DisplayName("Should retrieve skill list successfully")
        @WithMockUser(username = "user@example.com", authorities = {"ROLE_USER"})
        void shouldRetrieveSkillListSuccessfully() throws Exception {
            MvcResult result = mockMvc.perform(get(buildUriSkillList())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponse = result.getResponse().getContentAsString();
            SkillListDto actualList = objectMapper.readValue(actualResponse, SkillListDto.class);

            assertThat(actualList.skills()).isNotNull();
        }

        @Test
        @DisplayName("Should retrieve single skill successfully")
        @WithMockUser(username = "user@example.com", authorities = {"ROLE_USER"})
        void shouldRetrieveSingleSkillSuccessfully() throws Exception {
            Long skillId = 1L;

            MvcResult result = mockMvc.perform(get(buildUriSkillSingle(skillId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponse = result.getResponse().getContentAsString();
            SkillDto actualSkill = objectMapper.readValue(actualResponse, SkillDto.class);

            assertThat(actualSkill.id()).isEqualTo(skillId);
            assertThat(actualSkill.name()).isNotBlank();
        }

        @Test
        @DisplayName("Should return 404 when skill not found")
        @WithMockUser(username = "user@example.com", authorities = {"ROLE_USER"})
        void shouldReturn404WhenSkillNotFound() throws Exception {
            Long nonExistentSkillId = 999L;

            MvcResult result = mockMvc.perform(get(buildUriSkillSingle(nonExistentSkillId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn();

            String actualResponse = result.getResponse().getContentAsString();
            ErrorDto errorDto = objectMapper.readValue(actualResponse, ErrorDto.class);

            assertThat(errorDto.getMessage()).contains(SkillService.SKILL_NOT_FOUND_MSG);
            assertThat(errorDto.getMessage()).contains(nonExistentSkillId.toString());
        }
    }

    @Nested
    @DisplayName("Method Security Tests")
    class MethodSecurity {

        @Test
        @DisplayName("Should allow USER to access skill endpoints")
        @WithMockUser(authorities = {"ROLE_USER"})
        void shouldAllowUserToAccessSkillEndpoints() throws Exception {
            mockMvc.perform(get(buildUriSkillList())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());

            mockMvc.perform(get(buildUriSkillSingle(1L))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should deny access to anonymous users")
        @WithAnonymousUser
        void shouldDenyAccessToAnonymousUsers() throws Exception {
            mockMvc.perform(get(buildUriSkillList())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());

            mockMvc.perform(get(buildUriSkillSingle(1L))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow ADMIN to access skill endpoints")
        @WithMockUser(authorities = {"ROLE_ADMIN"})
        void shouldAllowAdminToAccessSkillEndpoints() throws Exception {
            mockMvc.perform(get(buildUriSkillList())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());

            mockMvc.perform(get(buildUriSkillSingle(1L))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}