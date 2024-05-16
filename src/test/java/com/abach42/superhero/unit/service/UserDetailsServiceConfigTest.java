package com.abach42.superhero.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.abach42.superhero.config.security.UserDetailsServiceConfig;
import com.abach42.superhero.dto.SuperheroUserDto;
import com.abach42.superhero.service.SuperheroUserService;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceConfigTest {

    @Mock
    private SuperheroUserService superheroUserService;

    private UserDetailsServiceConfig userDetailsServiceConfig;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.userDetailsServiceConfig = new UserDetailsServiceConfig();
    }

    @Test
    @DisplayName("User details service returns correct user details")
    public void userDetailsServiceReturnsCorrectUserDetails() {
        SuperheroUserDto mockUserDto = new SuperheroUserDto("test@example.com", "password123", "USER");
        given(superheroUserService.retrieveSuperheroUser(anyString())).willReturn(mockUserDto);

        UserDetails userDetails = userDetailsServiceConfig.userDetailsService(superheroUserService).loadUserByUsername("test@example.com");

        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }
}
