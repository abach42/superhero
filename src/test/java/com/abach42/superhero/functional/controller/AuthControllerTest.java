package com.abach42.superhero.functional.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.config.api.PathConfig;
import com.abach42.superhero.controller.AuthController;
import com.abach42.superhero.dto.TokenDto;
import com.abach42.superhero.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/*
 * Mocked rest client, regarding validation, mocked database
 *
 */
@WebMvcTest(AuthController.class)
@ContextConfiguration
@WebAppConfiguration
public class AuthControllerTest {

    private final static String PATH = PathConfig.TOKENS;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Login anonymous fails")
    @WithAnonymousUser
    public void testGetSuperheroLoginFails() throws Exception {
        mockMvc.perform(
                        get(PATH + "/login")
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login authenticated returns token")
    @WithMockUser
    public void testGetSuperheroAuthenticatedReturnsJwt() throws Exception {
        TokenDto expected = new TokenDto("foo", "Bearer", 1000, "bar");
        given(tokenService.generateTokenPair(any(Authentication.class))).willReturn(expected);

        MvcResult mvcResult = mockMvc.perform(
                        get(PATH + "/login")
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        TokenDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                TokenDto.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    //todo test jwt and refresh token by security
}