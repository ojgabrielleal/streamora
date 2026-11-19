package com.streamora.streamora.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;

import com.streamora.streamora.config.AuthCookieProperties;
import com.streamora.streamora.dto.auth.AccessTokenResponseDTO;
import com.streamora.streamora.dto.auth.AuthRequestDTO;
import com.streamora.streamora.dto.auth.AuthTokens;
import com.streamora.streamora.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTests {

	@Mock
	private AuthService authService;

	@Mock
	private AuthCookieProperties authCookieProperties;

	@InjectMocks
	private AuthController authController;

	@Test
	void exposesLoginRoute() throws NoSuchMethodException {
		PostMapping postMapping = AuthController.class
			.getMethod("auth", AuthRequestDTO.class)
			.getAnnotation(PostMapping.class);

		assertThat(postMapping.value())
			.containsExactly("/login");
	}

	@Test
	void delegatesAuthenticationToService() {
		AuthRequestDTO authRequest = new AuthRequestDTO("username", "password");
		AuthTokens generatedTokens = new AuthTokens("access-token", "refresh-token");

		when(authService.auth(authRequest))
			.thenReturn(generatedTokens);
		when(authCookieProperties.create("refresh-token"))
			.thenReturn(org.springframework.http.ResponseCookie.from("refresh", "refresh-token").build());

		ResponseEntity<AccessTokenResponseDTO> response = authController.auth(authRequest);

		assertThat(response.getBody())
			.isEqualTo(new AccessTokenResponseDTO("access-token"));

		verify(authService).auth(authRequest);
	}
}
