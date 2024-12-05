package com.myinsta.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Configure the HTTP Security (CORS, CSRF, session management, etc.)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Disable CSRF (only for testing/dev environments, enable in production with proper config)
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/api/users/**").permitAll() // Allow public access to the hello endpoint
                    .anyRequest().authenticated() // Require authentication for all other endpoints
            )
            .formLogin().permitAll(); // Allow form-based login (if needed)

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}