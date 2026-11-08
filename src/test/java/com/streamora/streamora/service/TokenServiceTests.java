package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

class TokenServiceTests {

	private static final String SECRET = "a-test-secret-that-is-long-enough-for-hmac-signing";
	private static final String USERNAME = "streamora-user";

	private final TokenService tokenService = new TokenService(Algorithm.HMAC256(SECRET));

	@Test
	void generatesAccessAndRefreshTokens() {
		Map<String, String> tokens = tokenService.generateTokens(USERNAME);

		assertThat(tokens).containsOnlyKeys("accessToken", "refreshToken");
		assertThat(tokens.values()).allSatisfy(token -> assertThat(token).isNotBlank());
	}

	@Test
	void storesUsernameAndImmutableUniqueIdentifiers() {
		Map<String, String> tokens = tokenService.generateTokens(USERNAME);
		DecodedJWT accessToken = JWT.decode(tokens.get("accessToken"));
		DecodedJWT refreshToken = JWT.decode(tokens.get("refreshToken"));

		assertThat(accessToken.getSubject()).isEqualTo(USERNAME);
		assertThat(refreshToken.getSubject()).isEqualTo(USERNAME);
		assertThat(accessToken.getId()).isNotBlank();
		assertThat(refreshToken.getId()).isNotBlank().isNotEqualTo(accessToken.getId());
	}

	@Test
	void usesExpectedExpirationForEachTokenType() {
		Map<String, String> tokens = tokenService.generateTokens(USERNAME);
		DecodedJWT accessToken = JWT.decode(tokens.get("accessToken"));
		DecodedJWT refreshToken = JWT.decode(tokens.get("refreshToken"));

		assertThat(Duration.between(accessToken.getIssuedAtAsInstant(), accessToken.getExpiresAtAsInstant()))
			.isEqualTo(Duration.ofMinutes(5));
		assertThat(Duration.between(refreshToken.getIssuedAtAsInstant(), refreshToken.getExpiresAtAsInstant()))
			.isEqualTo(Duration.ofDays(7));
	}

	@Test
	void delegatesGenerationToPrivateTokenSpecificMethods() throws Exception {
		Method accessTokenGenerator = TokenService.class
			.getDeclaredMethod("generateAccessToken", String.class);
		Method refreshTokenGenerator = TokenService.class
			.getDeclaredMethod("generateRefreshToken", String.class);

		assertThat(Modifier.isPrivate(accessTokenGenerator.getModifiers())).isTrue();
		assertThat(Modifier.isPrivate(refreshTokenGenerator.getModifiers())).isTrue();
	}

	@Test
	void validatesEachTokenOnlyWithItsExpectedType() {
		Map<String, String> tokens = tokenService.generateTokens(USERNAME);

		assertThat(tokenService.validateAccessToken(tokens.get("accessToken"))).isTrue();
		assertThat(tokenService.validateRefreshToken(tokens.get("refreshToken"))).isTrue();
		assertThat(tokenService.validateAccessToken(tokens.get("refreshToken"))).isFalse();
		assertThat(tokenService.validateRefreshToken(tokens.get("accessToken"))).isFalse();
	}

	@Test
	void rejectsTokensSignedWithAnotherKey() {
		String token = new TokenService(Algorithm.HMAC256("another-test-secret-for-hmac-signing"))
			.generateTokens(USERNAME)
			.get("accessToken");

		assertThat(tokenService.validateAccessToken(token)).isFalse();
	}

	@Test
	void rejectsExpiredMalformedOrBlankTokens() {
		String expiredToken = createToken(USERNAME, "token-id", "access", Instant.now().minusSeconds(1));

		assertThat(tokenService.validateAccessToken(expiredToken)).isFalse();
		assertThat(tokenService.validateAccessToken("not-a-jwt")).isFalse();
		assertThat(tokenService.validateAccessToken(" ")).isFalse();
		assertThat(tokenService.validateAccessToken(null)).isFalse();
	}

	@Test
	void rejectsTokensWithoutUsernameOrIdentifier() {
		String tokenWithoutUsername = createToken(null, "token-id", "access", Instant.now().plusSeconds(60));
		String tokenWithoutIdentifier = createToken(USERNAME, null, "access", Instant.now().plusSeconds(60));

		assertThat(tokenService.validateAccessToken(tokenWithoutUsername)).isFalse();
		assertThat(tokenService.validateAccessToken(tokenWithoutIdentifier)).isFalse();
	}

	@Test
	void rejectsBlankUsernameWhenGeneratingTokens() {
		assertThatThrownBy(() -> tokenService.generateTokens(" "))
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
