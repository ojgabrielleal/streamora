package com.streamora.streamora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI streamoraOpenApi() {
		return new OpenAPI()
			.info(new Info()
				.title("Streamora API")
				.description("API para gerenciamento de web-rádio Streamora.")
				.version("1.0.0"));
	}
}
