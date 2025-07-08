package com.abach42.superhero.login.authorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.abach42.superhero.user.ApplicationUser;
import com.abach42.superhero.user.ApplicationUserService;
import com.abach42.superhero.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@Tags(value = {@Tag("unit"), @Tag("auth")})
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceConfigTest {

    @Mock
    private ApplicationUserService applicationUserService;

    @Mock
    private ApplicationUser applicationUser;

    @InjectMocks
    private UserDetailsServiceConfig subject;

    @BeforeEach
    void setUp() {
        subject = new UserDetailsServiceConfig();
    }

    @Test
    @DisplayName("should create UserDetailsService bean")
    void shouldCreateUserDetailsServiceBean() {
        UserDetailsService userDetailsService = subject.userDetailsService(applicationUserService);

        assertThat(userDetailsService).isNotNull();
    }

    @Test
    @DisplayName("should load user by username with correct details")
    void shouldLoadUserByUsernameWithCorrectDetails() {
        String email = "test-user";
        String password = "test-password";
        UserRole role = UserRole.USER;

        given(applicationUserService.retrieveUserByEmail(email))
                .willReturn(applicationUser);
        given(applicationUser.getEmail()).willReturn(email);
        given(applicationUser.getPassword()).willReturn(password);
        given(applicationUser.getRole()).willReturn(role);

        UserDetailsService userDetailsService = subject.userDetailsService(applicationUserService);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("USER");
    }

    @Test
    @DisplayName("should handle admin role correctly")
    void shouldHandleAdminRoleCorrectly() {
        String email = "admin-user";
        String password = "admin-password";
        UserRole role = UserRole.ADMIN;

        given(applicationUserService.retrieveUserByEmail(email))
                .willReturn(applicationUser);
        given(applicationUser.getEmail()).willReturn(email);
        given(applicationUser.getPassword()).willReturn(password);
        given(applicationUser.getRole()).willReturn(role);

        UserDetailsService userDetailsService = subject.userDetailsService(applicationUserService);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("should create enabled user details")
    void shouldCreateEnabledUserDetails() {
        String email = "test-user";
        given(applicationUserService.retrieveUserByEmail(email))
                .willReturn(applicationUser);
        given(applicationUser.getEmail()).willReturn(email);
        given(applicationUser.getPassword()).willReturn("password");
        given(applicationUser.getRole()).willReturn(UserRole.USER);

        UserDetailsService userDetailsService = subject.userDetailsService(applicationUserService);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }
}
