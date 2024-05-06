package com.abach42.superhero.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.abach42.superhero.config.api.PathConfig;
import com.abach42.superhero.config.security.SecuredAdmin;
import com.abach42.superhero.config.security.SecuredUser;
import com.abach42.superhero.configuration.ObjectMapperSerializerHelper;
import com.abach42.superhero.configuration.TestContainerConfiguration;
import com.abach42.superhero.configuration.TestDataConfiguration;
import com.abach42.superhero.dto.ErrorDto;
import com.abach42.superhero.dto.SkillProfileDto;
import com.abach42.superhero.entity.SkillProfile;
import com.abach42.superhero.repository.SkillProfileRepository;
import com.abach42.superhero.repository.SkillRepository;
import com.abach42.superhero.service.SkillProfileService;
import com.abach42.superhero.service.SkillService;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * End to end test with real database and mock client
 * 
 * * Validations: Null fails
 * * Validations: Non null fails (missing field, missing payload)
 * * CRUD not found fails
 * * Write for admin only test
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SkillProfileControllerIntegrationTest {
    private final static String PATH = PathConfig.SKILLPROFILES;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SkillProfileRepository skillProfileRepository;

    @Autowired
    private SkillRepository skillRepository;

    //unable to mock on layer
    private RequestPostProcessor allAuthorities = SecurityMockMvcRequestPostProcessors.jwt()
                        .authorities(new SimpleGrantedAuthority(SecuredAdmin.ROLE_ADMIN),
                                new SimpleGrantedAuthority(SecuredUser.ROLE_USER)); 

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectMapperSerializerHelper superheroDtoSerializer;

    private URI buildUri(int superheroId) {
        return UriComponentsBuilder.fromUriString(PATH)
                .build(superheroId);
    }

    private ErrorDto getErrorDtoFromResponseBody(MvcResult mvcResult) throws IOException {
        ErrorDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDto.class);
        return actual;
    }

    @Nested
    @NestedTestConfiguration(EnclosingConfiguration.OVERRIDE)
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    @DisplayName("Test without database change")
    class NoDatabaseChangeTest {
        @Test
        @DisplayName("GET " + PATH + " (mockmvc + db) list all superhero skill profile fails when not found")
        public void testListSuperheroSkillProfilesFailsWhenNoSuperhero() throws Exception {
            skillProfileRepository.deleteAll();
    
            MvcResult mvcResult = mockMvc.perform(
                        get(buildUri(1)).accept(MediaType.APPLICATION_JSON)
                                .with(allAuthorities))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn();
    
            ErrorDto actual = getErrorDtoFromResponseBody(mvcResult);
            assertThat(actual.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(actual.getError()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
            assertThat(actual.getMessage()).startsWith(SkillProfileService.SKILL_PROFILES_SUPERHERO_NOT_FOUND_MSG);
            assertThat(actual.getPath()).isEqualTo(buildUri(1) + "");
        }

        
        @Test
        @DisplayName("GET " + PATH + "/{skillId} (mockmvc + db) show superhero skill profile fails when not found")
        public void testShowSuperheroSkillProfileFailsWhenNoSuperhero() throws Exception {
            skillProfileRepository.deleteAll();
    
            MvcResult mvcResult = mockMvc.perform(
                        get(buildUri(1) + "/" + 1)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(allAuthorities))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn();
    
            ErrorDto actual = getErrorDtoFromResponseBody(mvcResult);
            assertThat(actual.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(actual.getError()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
            assertThat(actual.getMessage()).startsWith(SkillProfileService.SKILL_PROFILE_SUPERHERO_NOT_FOUND_MSG);
            assertThat(actual.getPath()).isEqualTo(buildUri(1) + "/" + 1);
        }
    }

    @Test
    @DisplayName("POST " + PATH + " (mockmvc + db) create superhero skill profile fails when missing field")
    public void testCreateSuperheroSkillProfileFailsWhenMissingFieldInPayload() throws Exception {
            SkillProfile failingSkillProfile = TestDataConfiguration.getSkillProfileToCreateStub();
            failingSkillProfile.setIntensity(null);    
            mockMvc.perform(
                            post(buildUri(1))
                                    .content(superheroDtoSerializer.get().writeValueAsString(SkillProfileDto.fromDomain(failingSkillProfile)))
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(SecurityMockMvcRequestPostProcessors.jwt()))
                    .andDo(print())
                    .andExpect(status().isUnprocessableEntity());
    } 
       
    @Test
    @DisplayName("POST " + PATH + " (mockmvc + db) create superhero skill profile fails when payload contains skill object")
    public void testCreateSuperheroSkillProfileFailsWhenSkillInPayload() throws Exception {
            SkillProfile failingSkillProfile = TestDataConfiguration.getSkillProfileStub();    
            mockMvc.perform(
                            post(buildUri(1))
                                    .content(superheroDtoSerializer.get().writeValueAsString(SkillProfileDto.fromDomain(failingSkillProfile)))
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(SecurityMockMvcRequestPostProcessors.jwt()))
                    .andDo(print())
                    .andExpect(status().isUnprocessableEntity());
    }    

    @Test
    @DisplayName("POST " + PATH + " (mockmvc + db) create superhero skill profile fails when missing payload")
    public void testCreateSuperheroSkillProfileFailsWhenEmptyPayload() throws Exception {
            mockMvc.perform(
                            post(buildUri(1))
                            .content(objectMapper.writeValueAsString(null))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(SecurityMockMvcRequestPostProcessors.jwt()))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
    }   
    
    @Test
    @DisplayName("POST " + PATH + " (mockmvc + db) create superhero skill profile fails when not found")
    public void testCreateSuperheroSkillProfileFailsWhenSkillNotFound() throws Exception {
        skillRepository.deleteAll();

        MvcResult mvcResult = mockMvc.perform(
                    post(buildUri(1))
                            .content(superheroDtoSerializer.get().writeValueAsString(TestDataConfiguration.getSkillProfileToCreateStub()))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(allAuthorities))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorDto actual = getErrorDtoFromResponseBody(mvcResult);
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(actual.getError()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
        assertThat(actual.getMessage()).startsWith(SkillService.SKILL_NOT_FOUND_MSG);
        assertThat(actual.getPath()).isEqualTo(buildUri(1) + "");
    }

    @Test
    @DisplayName("POST " + PATH + " (mockmvc + db) create superhero skill profile returns created link")
    public void testCreateSuperheroSkillProfileReturnsCreatedLink() throws Exception {
            SkillProfile skillProfile = TestDataConfiguration.getSkillProfileToCreateStub();    
            MvcResult mvcResult = mockMvc.perform(
                            post(buildUri(1))
                                    .content(superheroDtoSerializer.get().writeValueAsString(SkillProfileDto.fromDomain(skillProfile)))
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(allAuthorities))
                    .andDo(print())
                    .andExpect(status().is2xxSuccessful())
                    .andReturn();

            SkillProfileDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                    SkillProfileDto.class);    
            String locationHeader = mvcResult.getResponse().getHeader("Location");
            assertThat(locationHeader).endsWith(buildUri(1) + "/" + actual.id());
    }

    @Test
    @DisplayName("PUT " + PATH  + "/{skillId} (mockmvc + db) update superhero skill profile fails when missing field")
    public void testUpdateSuperheroSkillProfileFailsWhenMissingFieldInPayload() throws Exception {
            mockMvc.perform(
                    put(buildUri(1) + "/" + 1)
                            .content(objectMapper.writeValueAsString(null))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(SecurityMockMvcRequestPostProcessors.jwt()))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT " + PATH + "/{skillId} (mockmvc + db) edit superhero skill profile fails when superhero not found")
    public void testUpdateSuperheroSkillProfileFailsWhenNoSuperheroFound() throws Exception {
        skillProfileRepository.deleteAll();

        MvcResult mvcResult = mockMvc.perform(
                    put(buildUri(1) + "/" + 1)
                            .content(objectMapper.writeValueAsString(TestDataConfiguration.getSkillProfileToUpdateStub()))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(allAuthorities))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorDto actual = getErrorDtoFromResponseBody(mvcResult);
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(actual.getError()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
        assertThat(actual.getMessage()).startsWith(SkillProfileService.SKILL_PROFILE_SUPERHERO_NOT_FOUND_MSG);
        assertThat(actual.getPath()).isEqualTo(buildUri(1) + "/" + 1);
    }

    @Test
    @DisplayName("DELETE " + PATH + "/{skillId} (mockmvc + db) delete superhero skill profile fails when not found")
    public void testDeleteSuperheroSkillProfileFailsWhenNotFound() throws Exception {
        skillProfileRepository.deleteAll();

        MvcResult mvcResult = mockMvc.perform(
                        delete(buildUri(1) + "/" + 1)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .with(allAuthorities))
                        .andDo(print())
                        .andExpect(status().isNotFound())
                        .andReturn();

        ErrorDto actual = getErrorDtoFromResponseBody(mvcResult);
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(actual.getError()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
        assertThat(actual.getMessage()).startsWith(SkillProfileService.SKILL_PROFILE_SUPERHERO_NOT_FOUND_MSG);
        assertThat(actual.getPath()).isEqualTo(buildUri(1) + "/" + 1);
    }

    @Nested
    @NestedTestConfiguration(EnclosingConfiguration.INHERIT)
    @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
    @DisplayName("Test Roles")
    class RolesTest {

        @ParameterizedTest
        @MethodSource("adminOnlyProvider")
        @DisplayName("POST " + PATH + " (mockmvc + db) according to roles")
        public void testPost(Collection<GrantedAuthority> authorities, ResultMatcher status) throws Exception {

                mockMvc.perform(
                        request(HttpMethod.POST, buildUri(1))
                                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(authorities))
                                .content(objectMapper
                                        .writeValueAsString(TestDataConfiguration.getSkillProfileToCreateStub()))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status);
        }

        @ParameterizedTest
        @MethodSource("adminOnlyProvider")
        @DisplayName("PUT " + PATH + "/{skillId} (mockmvc + db) according to roles")
        public void testPut(Collection<GrantedAuthority> authorities, ResultMatcher status) throws Exception {
                mockMvc.perform(
                                request(HttpMethod.PUT, buildUri(1) + "/" + 1)
                                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                                                .authorities(authorities))
                                                .content(objectMapper
                                                                .writeValueAsString(TestDataConfiguration.getSkillProfileToUpdateStub()))
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status);
        }

        @ParameterizedTest
        @MethodSource("adminOnlyProvider")
        @DisplayName("DELETE " + PATH + "/{skillId} (mockmvc + db) according to roles")
        public void testDelete(Collection<GrantedAuthority> authorities, ResultMatcher status) throws Exception {
                mockMvc.perform(
                                request(HttpMethod.DELETE, buildUri(1) + "/" + 1)
                                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                                                .authorities(authorities))
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status);
        }

        private static Stream<Arguments> adminOnlyProvider() {
                return Stream.of(
                                Arguments.of(Arrays.asList(new SimpleGrantedAuthority(SecuredUser.ROLE_USER)),
                                                MockMvcResultMatchers.status().isForbidden()),
                                Arguments.of(Arrays.asList(new SimpleGrantedAuthority(SecuredAdmin.ROLE_ADMIN),
                                                new SimpleGrantedAuthority(SecuredUser.ROLE_USER)),
                                                MockMvcResultMatchers.status().is2xxSuccessful()));
        }
    }
}