package com.streamora.streamora.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieProperties {

	private static final String COOKIE_NAME = "streamora_refresh_token";
	private static final String COOKIE_PATH = "/auth";
	private static final Duration COOKIE_DURATION = Duration.ofDays(7);

	private final boolean secure;

	public AuthCookieProperties(
		@Value("${streamora.auth.refresh-cookie-secure:true}") boolean secure
	)
	{
		this.secure = secure;
	}

	public String name() {
		return COOKIE_NAME;
	}

	public ResponseCookie create(String refreshToken) {
		return cookieBuilder()
			.value(refreshToken)
			.maxAge(COOKIE_DURATION)
			.build();
	}

	public ResponseCookie expire() {
		return cookieBuilder()
			.value("")
			.maxAge(Duration.ZERO)
			.build();
	}

	private ResponseCookie.ResponseCookieBuilder cookieBuilder() {
		return ResponseCookie.from(COOKIE_NAME)
			.httpOnly(true)
			.secure(secure)
			.sameSite("Lax")
			.path(COOKIE_PATH);
	}
}
