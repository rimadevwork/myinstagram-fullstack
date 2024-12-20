package com.myinsta.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author rima.devwork@gmail.com 
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/*
	 *  Configure the HTTP Security (CORS, CSRF, session management, etc.)
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.csrf(csrf -> csrf.disable()).authorizeHttpRequests(
				auth -> auth.requestMatchers("/api/users/hello", "/api/users/register", "/api/users/validateUser").permitAll()
				//.requestMatchers("/api/users/updateProfile").hasRole("ROLE_USER")
				.anyRequest().authenticated()).build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}