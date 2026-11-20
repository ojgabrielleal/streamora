package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.streamora.streamora.dto.auth.AuthTokens;

class JwtServiceTests {

	private static final String SECRET = "a-test-secret-that-is-long-enough-for-hmac-signing";
	private static final String USERNAME = "streamora-user";

	private final JwtService jwtService = new JwtService(Algorithm.HMAC256(SECRET));

	@Test
	void generatesAccessAndRefreshTokens() {
		AuthTokens tokens = jwtService.generateTokens(USERNAME);

		assertThat(tokens.accessToken()).isNotBlank();
		assertThat(tokens.refreshToken()).isNotBlank();
	}

	@Test
	void storesUsernameAndImmutableUniqueIdentifiers() {
		AuthTokens tokens = jwtService.generateTokens(USERNAME);
		DecodedJWT accessToken = JWT.decode(tokens.accessToken());
		DecodedJWT refreshToken = JWT.decode(tokens.refreshToken());

		assertThat(accessToken.getSubject()).isEqualTo(USERNAME);
		assertThat(refreshToken.getSubject()).isEqualTo(USERNAME);
		assertThat(accessToken.getId()).isNotBlank();
		assertThat(refreshToken.getId()).isNotBlank().isNotEqualTo(accessToken.getId());
	}

	@Test
	void usesExpectedExpirationForEachTokenType() {
		AuthTokens tokens = jwtService.generateTokens(USERNAME);
		DecodedJWT accessToken = JWT.decode(tokens.accessToken());
		DecodedJWT refreshToken = JWT.decode(tokens.refreshToken());

		assertThat(Duration.between(accessToken.getIssuedAtAsInstant(), accessToken.getExpiresAtAsInstant()))
			.isEqualTo(Duration.ofMinutes(5));
		assertThat(Duration.between(refreshToken.getIssuedAtAsInstant(), refreshToken.getExpiresAtAsInstant()))
			.isEqualTo(Duration.ofDays(7));
	}

	@Test
	void delegatesGenerationToPrivateTokenSpecificMethods() throws Exception {
		Method accessTokenGenerator = JwtService.class
			.getDeclaredMethod("generateAccessToken", String.class);
		Method refreshTokenGenerator = JwtService.class
			.getDeclaredMethod("generateRefreshToken", String.class);

		assertThat(Modifier.isPrivate(accessTokenGenerator.getModifiers())).isTrue();
		assertThat(Modifier.isPrivate(refreshTokenGenerator.getModifiers())).isTrue();
	}

	@Test
	void validatesOnlyAccessTokenAsAccessToken() {
		AuthTokens tokens = jwtService.generateTokens(USERNAME);

		boolean validAccessToken = jwtService.validateAccessToken(tokens.accessToken());
		boolean invalidRefreshToken = jwtService.validateAccessToken(tokens.refreshToken());

		assertThat(validAccessToken).isTrue();
		assertThat(invalidRefreshToken).isFalse();
	}

	@Test
	void validatesOnlyRefreshTokenAsRefreshToken() {
		AuthTokens tokens = jwtService.generateTokens(USERNAME);

		boolean validRefreshToken = jwtService.validateRefreshToken(tokens.refreshToken());
		boolean invalidAccessToken = jwtService.validateRefreshToken(tokens.accessToken());

		assertThat(validRefreshToken).isTrue();
		assertThat(invalidAccessToken).isFalse();
	}

	@Test
	void rejectsTokensSignedWithAnotherKey() {
		String token = new JwtService(Algorithm.HMAC256("another-test-secret-for-hmac-signing"))
			.generateTokens(USERNAME)
			.accessToken();

		boolean validToken = jwtService.validateAccessToken(token);

		assertThat(validToken).isFalse();
	}

	@Test
	void validatesRefreshTokenAndGetsUsernameForSessionRenewal() {
		AuthTokens tokens = jwtService.generateTokens(USERNAME);

		assertThat(jwtService.validateRefreshTokenAndGetUsernameForSessionRenewal(tokens.refreshToken()))
			.contains(USERNAME);
		assertThat(jwtService.validateRefreshTokenAndGetUsernameForSessionRenewal(tokens.accessToken()))
			.isEmpty();
		assertThat(jwtService.validateRefreshTokenAndGetUsernameForSessionRenewal("invalid"))
			.isEmpty();
	}

	@Test
	void rejectsExpiredMalformedOrBlankTokens() {
		String expiredToken = createToken(USERNAME, "token-id", "access", Instant.now().minusSeconds(1));

		boolean validExpiredToken = jwtService.validateAccessToken(expiredToken);
		boolean validMalformedToken = jwtService.validateAccessToken("not-a-jwt");
		boolean validBlankToken = jwtService.validateAccessToken(" ");
		boolean validNullToken = jwtService.validateAccessToken(null);

		assertThat(validExpiredToken).isFalse();
		assertThat(validMalformedToken).isFalse();
		assertThat(validBlankToken).isFalse();
		assertThat(validNullToken).isFalse();
	}

	@Test
	void rejectsTokensWithoutUsernameOrIdentifier() {
		String tokenWithoutUsername = createToken(null, "token-id", "access", Instant.now().plusSeconds(60));
		String tokenWithoutIdentifier = createToken(USERNAME, null, "access", Instant.now().plusSeconds(60));

		boolean validTokenWithoutUsername = jwtService.validateAccessToken(tokenWithoutUsername);
		boolean validTokenWithoutIdentifier = jwtService.validateAccessToken(tokenWithoutIdentifier);

		assertThat(validTokenWithoutUsername).isFalse();
		assertThat(validTokenWithoutIdentifier).isFalse();
	}

	@Test
	void rejectsBlankUsernameWhenGeneratingTokens() {
		assertThatThrownBy(() -> jwtService.generateTokens(" "))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Username must not be blank");
	}

	private String createToken(String subject, String jwtId, String tokenType, Instant expiresAt) {
		return JWT.create()
			.withSubject(subject)
			.withJWTId(jwtId)
			.withExpiresAt(expiresAt)
			.withClaim("token_type", tokenType)
			.sign(Algorithm.HMAC256(SECRET));
	}
}
