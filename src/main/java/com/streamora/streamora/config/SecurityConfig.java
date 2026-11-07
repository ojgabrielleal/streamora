package com.streamora.streamora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	private static final String[] PUBLIC_DOCUMENTATION_PATHS = {
		"/v3/api-docs",
		"/v3/api-docs/**",
		"/swagger-ui.html",
		"/swagger-ui/**"
	};

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(HttpMethod.GET, PUBLIC_DOCUMENTATION_PATHS).permitAll()
				.anyRequest().authenticated()
			)
			.formLogin(AbstractHttpConfigurer::disable)
			.build();
	}
}

