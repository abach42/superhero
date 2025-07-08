package com.abach42.superhero.login.authentication;

import static com.abach42.superhero.config.api.PathConfig.BASE_URI;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.login.token.TokenResponseDto;
import com.abach42.superhero.login.token.TokenResponseDto.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    private static final String SLUG = "/auth";

    String basePath = BASE_URI;

    String slug;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setSlug() {
        slug = basePath + SLUG;
    }

    private void performTokenRequestAndAssert(String url, String accessToken, String refreshToken,
            int expiresIn) throws Exception {
        TokenResponseDto responseDto = new TokenResponseDto(
                accessToken,
                TokenType.BEARER,
                expiresIn,
                refreshToken
        );

        when(authenticationService.createNewTokenPair(any(Authentication.class)))
                .thenReturn(responseDto);

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(accessToken))
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(expiresIn))
                .andExpect(jsonPath("$.refresh_token").value(refreshToken))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    @DisplayName("USER role: should get token from /login")
    void userCanLogin() throws Exception {
        performTokenRequestAndAssert(
                slug + "/login",
                "user-access-token",
                "user-refresh-token",
                3600
        );
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    @DisplayName("ADMIN role: should get token from /login")
    void adminCanLogin() throws Exception {
        performTokenRequestAndAssert(
                slug + "/login",
                "admin-access-token",
                "admin-refresh-token",
                3600
        );
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    @DisplayName("USER role: should get token from /refresh-token")
    void userCanRefreshToken() throws Exception {
        performTokenRequestAndAssert(
                slug + "/refresh-token",
                "user-refresh-access",
                "user-new-refresh",
                1800
        );
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    @DisplayName("ADMIN role: should get token from /refresh-token")
    void adminCanRefreshToken() throws Exception {
        performTokenRequestAndAssert(
                slug + "/refresh-token",
                "admin-refresh-access",
                "admin-new-refresh",
                1800
        );
    }
}
