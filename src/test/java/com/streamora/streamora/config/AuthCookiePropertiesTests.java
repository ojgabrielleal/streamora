package com.streamora.streamora.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

class AuthCookiePropertiesTests {

	@Test
	void createsProtectedRefreshCookie() {
		AuthCookieProperties properties = new AuthCookieProperties(true);

		ResponseCookie cookie = properties.create("refresh-token");

		assertThat(cookie.getName()).isEqualTo("streamora_refresh_token");
		assertThat(cookie.getValue()).isEqualTo("refresh-token");
		assertThat(cookie.isHttpOnly()).isTrue();
		assertThat(cookie.isSecure()).isTrue();
		assertThat(cookie.getSameSite()).isEqualTo("Lax");
		assertThat(cookie.getPath()).isEqualTo("/auth");
		assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofDays(7));
	}

	@Test
	void expiresCookieWithMatchingAttributes() {
		AuthCookieProperties properties = new AuthCookieProperties(true);

		ResponseCookie cookie = properties.expire();

		assertThat(cookie.getValue()).isEmpty();
		assertThat(cookie.getMaxAge()).isZero();
		assertThat(cookie.getPath()).isEqualTo("/auth");
		assertThat(cookie.isHttpOnly()).isTrue();
		assertThat(cookie.isSecure()).isTrue();
	}
}
