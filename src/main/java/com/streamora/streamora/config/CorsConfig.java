package com.streamora.streamora.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

	@Bean
	CorsConfigurationSource corsConfigurationSource(
		@Value("${streamora.cors.allowed-origins}") List<String> allowedOrigins
	)
	{
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOrigins(allowedOrigins);
		corsConfiguration.setAllowedMethods(List.of(
			HttpMethod.GET.name(),
			HttpMethod.POST.name(),
			HttpMethod.PUT.name(),
			HttpMethod.DELETE.name(),
			HttpMethod.OPTIONS.name()
		));
		corsConfiguration.setAllowedHeaders(List.of(
			HttpHeaders.AUTHORIZATION,
			HttpHeaders.CONTENT_TYPE
		));

		UrlBasedCorsConfigurationSource corsConfigurationSource =
			new UrlBasedCorsConfigurationSource();
		corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

		return corsConfigurationSource;
	}
}
