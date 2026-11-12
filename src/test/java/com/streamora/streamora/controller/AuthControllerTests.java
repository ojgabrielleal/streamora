package com.streamora.streamora.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.PostMapping;

import com.streamora.streamora.dto.AuthRequestDTO;
import com.streamora.streamora.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTests {

	@Mock
	private AuthService authService;

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
		Map<String, String> generatedTokens = Map.of(
			"accessToken", "access-token",
			"refreshToken", "refresh-token"
		);

		when(authService.auth(authRequest))
			.thenReturn(generatedTokens);

		assertThat(authController.auth(authRequest))
			.isSameAs(generatedTokens);

		verify(authService).auth(authRequest);
	}
}
