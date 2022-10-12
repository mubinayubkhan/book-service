package com.assignment.bookservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfiguration.java
 *
 * Configuring System Security Authentication Rules
 *
 */
@Configuration
public class SecurityConfiguration {

    /**
     * Setting up the following Security Configuration,
     * 1. DELETE request only if user is Admin
     * 2. All other requests are permitted without Authentication
     * 3. Disable FrameOptions to enable H2-Console Database access
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.DELETE).hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()
                .httpBasic();
        http.headers().frameOptions().disable();
        return http.build();
    }

    /**
     * Setting up Admin Authentication Credentials
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User
                .withUsername("admin")
                .password("{noop}password")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
