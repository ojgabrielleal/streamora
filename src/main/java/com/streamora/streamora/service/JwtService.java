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
import com.streamora.streamora.entity.UserRole;

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
		return generateTokens(username, null);
	}

	public AuthTokens generateTokens(String username, UserRole role) {
		if (username == null || username.isBlank()) {
			throw new IllegalArgumentException("Username must not be blank");
		}

		return new AuthTokens(
			generateAccessToken(username, role),
			generateRefreshToken(username, role)
		);
	}

	public boolean validateAccessToken(String token) {
		if (token == null || token.isBlank()) {
			return false;
		}

		return validateTokens(token, ACCESS_TOKEN_TYPE);
	}

	public Optional<String> validateAccessTokenAndGetUsername(String token) {
		return decodeToken(token, ACCESS_TOKEN_TYPE)
			.map(decodedToken -> decodedToken.getSubject());
	}

	public Optional<UserRole> validateAccessTokenAndGetRole(String token) {
		return decodeToken(token, ACCESS_TOKEN_TYPE)
			.map(decodedToken -> decodedToken.getClaim("role").asString())
			.filter(role -> role != null && !role.isBlank())
			.map(UserRole::valueOf);
	}

	public boolean validateRefreshToken(String token) {
		if (token == null || token.isBlank()) {
			return false;
		}

		return validateTokens(token, REFRESH_TOKEN_TYPE);
	}

	public Optional<String> validateRefreshTokenAndGetUsernameForSessionRenewal(String token) {
		return decodeToken(token, REFRESH_TOKEN_TYPE)
			.map(decodedToken -> decodedToken.getSubject());
	}

	@SuppressWarnings("unused")
	private String generateAccessToken(String username) {
		return generateAccessToken(username, null);
	}

	private String generateAccessToken(String username, UserRole role) {
		return generateToken(
			username,
			ACCESS_TOKEN_TYPE,
			role,
			Instant.now().plus(ACCESS_TOKEN_EXPIRATION_MINUTES, ChronoUnit.MINUTES)
		);
	}

	@SuppressWarnings("unused")
	private String generateRefreshToken(String username) {
		return generateRefreshToken(username, null);
	}

	private String generateRefreshToken(String username, UserRole role) {
		return generateToken(
			username,
			REFRESH_TOKEN_TYPE,
			role,
			Instant.now().plus(REFRESH_TOKEN_EXPIRATION_DAYS, ChronoUnit.DAYS)
		);
	}

	private String generateToken(
		String username,
		String tokenType,
		UserRole role,
		Instant expiresAt
	)
	{
		var builder = JWT.create()
			.withSubject(username)
			.withJWTId(UUID.randomUUID().toString())
			.withIssuedAt(Instant.now())
			.withExpiresAt(expiresAt)
			.withClaim("token_type", tokenType);

		if (role != null) {
			builder.withClaim("role", role.name());
		}

		return builder.sign(algorithm);
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
