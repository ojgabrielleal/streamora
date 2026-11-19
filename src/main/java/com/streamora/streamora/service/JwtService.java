package com.streamora.streamora.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.streamora.streamora.dto.auth.AuthTokens;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	private static final long ACCESS_TOKEN_EXPIRATION_MINUTES = 5;
	private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7;
	private static final String ACCESS_TOKEN_TYPE = "access";
	private static final String REFRESH_TOKEN_TYPE = "refresh";

	private final Algorithm algorithm;

	public AuthTokens generateTokens(String username) {
		if (username == null || username.isBlank()) {
			throw new IllegalArgumentException("Username must not be blank");
		}

		return new AuthTokens(
			generateAccessToken(username),
			generateRefreshToken(username)
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

	public Optional<String> getRefreshTokenSubject(String token) {
		return decodeToken(token, REFRESH_TOKEN_TYPE)
			.map(DecodedJWT::getSubject);
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
		return decodeToken(token, tokenType).isPresent();
	}

	private Optional<DecodedJWT> decodeToken(String token, String tokenType) {
		if (token == null || token.isBlank()) {
			return Optional.empty();
		}

		try {
			JWTVerifier verifier = JWT.require(algorithm)
				.withClaim("token_type", tokenType)
				.build();

			DecodedJWT decodedToken = verifier.verify(token);

			boolean validClaims = decodedToken.getSubject() != null
				&& !decodedToken.getSubject().isBlank()
				&& decodedToken.getId() != null
				&& !decodedToken.getId().isBlank();

			return validClaims ? Optional.of(decodedToken) : Optional.empty();
		} catch (JWTVerificationException exception) {
			return Optional.empty();
		}
	}

}
