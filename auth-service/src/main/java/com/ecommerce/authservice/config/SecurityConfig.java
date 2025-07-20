package com.ecommerce.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // This bean defines the security filter chain (how requests are secured)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for API endpoints (common in stateless microservices)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated() // All requests require authentication
                )
                .httpBasic(org.springframework.security.config.Customizer.withDefaults()); // Enable HTTP Basic Auth for login pop-up

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // WARNING: This PasswordEncoder is for development/testing ONLY.
        // It allows plain text passwords. DO NOT USE IN PRODUCTION ENVIRONMENTS.
        // In production, you should use BCryptPasswordEncoder or similar for strong hashing.
        return NoOpPasswordEncoder.getInstance();
    }

    // Spring Security will automatically pick up the user from spring.security.user.*
    // when an AuthenticationProvider uses a UserDetailsService and a PasswordEncoder,
    // which the auto-configuration does by default if no custom UserDetailsService is provided.
}
