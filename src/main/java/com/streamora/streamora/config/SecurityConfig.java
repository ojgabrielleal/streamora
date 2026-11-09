package com.streamora.streamora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.streamora.streamora.filter.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {
	private static final String[] PUBLIC_DOCUMENTATION_PATHS = {
		"/v3/api-docs",
		"/v3/api-docs/**",
		"/swagger-ui.html",
		"/swagger-ui/**"
	};

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
		http
			.formLogin(form->form.disable())
			.csrf(csrf -> csrf.disable())
			.exceptionHandling(exception -> exception.authenticationEntryPoint(
				new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
			)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(HttpMethod.GET, PUBLIC_DOCUMENTATION_PATHS).permitAll()
				.requestMatchers(HttpMethod.POST, "/login").permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(
				jwtAuthenticationFilter,
				UsernamePasswordAuthenticationFilter.class
			);

		return http.build();
	}
}

