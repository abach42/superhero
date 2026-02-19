package com.abach42.superhero.shared.security;

import static com.abach42.superhero.shared.api.PathConfig.AUTH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.testconfiguration.TestContainerConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = {TestContainerConfiguration.class})
@AutoConfigureMockMvc
@Testcontainers
public class CorsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should allow OPTIONS request for login endpoint")
    void shouldAllowOptionsRequestForLogin() throws Exception {
        String uri = UriComponentsBuilder.fromPath(AUTH)
                .pathSegment("login")
                .toUriString();

        mockMvc.perform(options(uri)
                        .header("Origin", "http://localhost:3000")
                        .header("Referer", "http://localhost:3000/ ")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET"))
                .andExpect(header().string("Access-Control-Allow-Headers", "authorization"))
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }
}
