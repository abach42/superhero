package com.abach42.superhero.login.authorization;

import com.abach42.superhero.user.ApplicationUser;
import com.abach42.superhero.user.ApplicationUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class UserDetailsServiceConfig {

    @Bean
    public UserDetailsService userDetailsService(ApplicationUserService applicationUserService) {
        return customerUserId -> {
            ApplicationUser applicationUser =
                    applicationUserService.retrieveUserByEmail(customerUserId);

            return User.withUsername(applicationUser.getEmail())
                    .password(applicationUser.getPassword())
                    .authorities(applicationUser.getRole().name())
                    .build();
        };
    }
}