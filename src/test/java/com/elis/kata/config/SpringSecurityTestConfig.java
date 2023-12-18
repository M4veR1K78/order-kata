package com.elis.kata.config;

import com.elis.kata.application.common.ConnectedUser;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

@TestConfiguration
public class SpringSecurityTestConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        List<ConnectedUser> users = List.of(
            new ConnectedUser("externalUser", List.of(new SimpleGrantedAuthority("EXTERNAL_USER"))),
            new ConnectedUser("internalUser", List.of(new SimpleGrantedAuthority("INTERNAL_USER")))
        );
        return username -> findByUsername(users, username);
    }

    private ConnectedUser findByUsername(List<ConnectedUser> users, String username) {
        return users.stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }
}
