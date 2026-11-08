package com.streamora.streamora.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.auth0.jwt.algorithms.Algorithm;

@Configuration
public class JwtConfig {

	@Bean
	Algorithm jwtAlgorithm(@Value("${jwt.secret}") String secret) {
		return Algorithm.HMAC256(secret);
	}
}
