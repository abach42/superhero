package com.abach42.superhero.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.config.api.PathConfig;
import com.abach42.superhero.config.security.SecuredAdmin;
import com.abach42.superhero.config.security.SecuredUser;
import com.abach42.superhero.configuration.TestContainerConfiguration;
import com.abach42.superhero.dto.ErrorDto;
import com.abach42.superhero.repository.SkillRepository;
import com.abach42.superhero.service.SkillService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.junit.jupiter.Testcontainers;

/*
 * Integration test with real database and mock client
 *
 * * not found fails
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SkillControllerIntegrationTest {

    private final static String PATH = PathConfig.SKILLS;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SkillRepository skillRepository;

    //unable to mock on layer
    private RequestPostProcessor allAuthorities = SecurityMockMvcRequestPostProcessors.jwt()
            .authorities(new SimpleGrantedAuthority(SecuredAdmin.ROLE_ADMIN),
                    new SimpleGrantedAuthority(SecuredUser.ROLE_USER));

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET " + PATH + " (mockmvc + db) list all skills fails when not found")
    public void testListSkillsFailsWhenNoSkill() throws Exception {
        skillRepository.deleteAll();

        MvcResult mvcResult = mockMvc.perform(
                        get(PATH).accept(MediaType.APPLICATION_JSON)
                                .with(allAuthorities))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorDto actual = getErrorDtoFromResultPayload(mvcResult);
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(), SkillService.SKILLS_NOT_FOUND_MSG,
                        PATH));
    }

    private ErrorDto getErrorDtoFromResultPayload(MvcResult mvcResult)
            throws IOException {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                ErrorDto.class);
    }

    @Test
    @DisplayName("GET " + PATH + "/1 (mockmvc + db) show a skill fails when not found")
    public void testShowSkillFailsWhenNoSkill() throws Exception {
        skillRepository.deleteAll();

        MvcResult mvcResult = mockMvc.perform(
                        get(PATH + "/" + 1)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(allAuthorities))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorDto actual = getErrorDtoFromResultPayload(mvcResult);
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(new ErrorDto(HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        SkillService.SKILL_NOT_FOUND_MSG + 1, PATH + "/" + 1));
    }
}