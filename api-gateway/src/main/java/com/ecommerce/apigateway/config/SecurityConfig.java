package com.ecommerce.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity // This annotation enables Spring Security's reactive web security features
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF for reactive gateway
                .authorizeExchange(exchanges -> exchanges
                        // Permit all requests to Product Service and Category Service paths
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/api/products/**").permitAll()
                        .pathMatchers("/api/categories/**").permitAll()
                        // If Auth Service login/registration endpoints should also be public via gateway:
                        .pathMatchers("/api/auth/**").permitAll()
                        .anyExchange().authenticated() // All other requests require authentication
                );
        // .httpBasic(ServerHttpSecurity.HttpBasicSpec::withDefaults); // Uncomment if you want Basic Auth for other secured routes on the Gateway itself

        return http.build();
    }

    // NO PasswordEncoder needed here unless the Gateway itself is doing user management,
    // which is typically not the case for a gateway acting as a reverse proxy/JWT validator.
}