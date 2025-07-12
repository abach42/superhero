package com.abach42.superhero.login.authorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.abach42.superhero.user.ApplicationUser;
import com.abach42.superhero.user.ApplicationUserService;
import com.abach42.superhero.user.UserNotFoundException;
import com.abach42.superhero.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Tags(value = {@Tag("unit"), @Tag("auth")})
@SpringBootTest
@ContextConfiguration(classes = {UserDetailsServiceConfig.class})
class UserDetailsServiceConfigTest {

    @MockitoBean
    private ApplicationUserService applicationUserService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Should load user with USER role correctly")
    void shouldLoadUserWithUserRoleCorrectly() {
        String email = "user@example.com";
        String password = "password123";
        ApplicationUser user = new ApplicationUser(email, password, UserRole.USER);

        given(applicationUserService.retrieveUserByEmail(email)).willReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo(
                "USER");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }

    @Test
    @DisplayName("Should load user with ADMIN role correctly")
    void shouldLoadUserWithAdminRoleCorrectly() {
        String email = "admin@example.com";
        String password = "adminpass";
        ApplicationUser admin = new ApplicationUser(email, password, UserRole.ADMIN);

        given(applicationUserService.retrieveUserByEmail(email)).willReturn(admin);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo(
                "ADMIN");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found")
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        String email = "nonexistent@example.com";

        given(applicationUserService.retrieveUserByEmail(email))
                .willThrow(new UserNotFoundException(email));

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Should handle null email gracefully")
    void shouldHandleNullEmailGracefully() {
        given(applicationUserService.retrieveUserByEmail(null))
                .willThrow(new UserNotFoundException("null"));

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(null))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Should handle empty email gracefully")
    void shouldHandleEmptyEmailGracefully() {
        String emptyEmail = "";

        given(applicationUserService.retrieveUserByEmail(emptyEmail))
                .willThrow(new UserNotFoundException(emptyEmail));

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(emptyEmail))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Should create UserDetailsService bean")
    void shouldCreateUserDetailsServiceBean() {
        assertThat(userDetailsService).isNotNull();
        assertThat(userDetailsService).isInstanceOf(UserDetailsService.class);
    }

    @Test
    @DisplayName("Should handle different user roles consistently")
    void shouldHandleDifferentUserRolesConsistently() {
        String email = "complete@example.com";
        String password = "secure123";
        ApplicationUser user = new ApplicationUser(email, password, UserRole.USER);

        given(applicationUserService.retrieveUserByEmail(email)).willReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(userDetails.getAuthorities())
                .hasSize(1)
                .extracting("authority")
                .containsExactly("USER");

        // Verify default UserDetails flags
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }
}