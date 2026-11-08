package com.streamora.streamora.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

	private static final long ACCESS_TOKEN_EXPIRATION_MINUTES = 5;
	private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7;
	private static final String ACCESS_TOKEN_TYPE = "access";
	private static final String REFRESH_TOKEN_TYPE = "refresh";

	private final Algorithm algorithm;

	public Map<String, String> generateTokens(String username) {
		if (username == null || username.isBlank()) {
			throw new IllegalArgumentException("Username must not be blank");
		}

		return Map.of(
			"accessToken", generateAccessToken(username),
			"refreshToken", generateRefreshToken(username)
		);
	}

	public boolean validateAccessToken(String token) {
		if (token == null || token.isBlank()) {
			return false;
		}

		return validateTokens(token, ACCESS_TOKEN_TYPE);
	}

	public boolean validateRefreshToken(String token) {
		if (token == null || token.isBlank()) {
			return false;
		}

		return validateTokens(token, REFRESH_TOKEN_TYPE);
	}

	private String generateAccessToken(String username) {
		return generateToken(
			username,
			ACCESS_TOKEN_TYPE,
			Instant.now().plus(ACCESS_TOKEN_EXPIRATION_MINUTES, ChronoUnit.MINUTES)
		);
	}

	private String generateRefreshToken(String username) {
		return generateToken(
			username,
			REFRESH_TOKEN_TYPE,
			Instant.now().plus(REFRESH_TOKEN_EXPIRATION_DAYS, ChronoUnit.DAYS)
		);
	}

	private String generateToken(String username, String tokenType, Instant expiresAt) {
		return JWT.create()
			.withSubject(username)
			.withJWTId(UUID.randomUUID().toString())
			.withIssuedAt(Instant.now())
			.withExpiresAt(expiresAt)
			.withClaim("token_type", tokenType)
			.sign(algorithm);
	}

	private boolean validateTokens(String token, String tokenType) {
		try {
			JWTVerifier verifier = JWT.require(algorithm)
				.withClaim("token_type", tokenType)
				.build();

			DecodedJWT decodedToken = verifier.verify(token);

			return decodedToken.getSubject() != null
				&& !decodedToken.getSubject().isBlank()
				&& decodedToken.getId() != null
				&& !decodedToken.getId().isBlank();
		} catch (JWTVerificationException exception) {
			return false;
		}
	}

}
