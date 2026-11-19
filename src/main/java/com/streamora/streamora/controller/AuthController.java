package com.streamora.streamora.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RestController;

import com.streamora.streamora.config.AuthCookieProperties;
import com.streamora.streamora.dto.auth.AccessTokenResponseDTO;
import com.streamora.streamora.dto.auth.AuthRequestDTO;
import com.streamora.streamora.dto.auth.AuthTokens;
import com.streamora.streamora.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Autenticação de usuários")
public class AuthController {

	private final AuthService authService;
	private final AuthCookieProperties authCookieProperties;

	@PostMapping("/login")
	@Operation(summary = "Autenticar usuário")
	@ApiResponse(responseCode = "200", description = "Usuário autenticado")
	@ApiResponse(
		responseCode = "401",
		description = "Credenciais inválidas",
		content = @Content(
			schema = @Schema(hidden = true)
		)
	)
	public ResponseEntity<AccessTokenResponseDTO> auth(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Credenciais do usuário", required = true)
		@RequestBody AuthRequestDTO authRequest
	)
	{
		AuthTokens authTokens = authService.auth(authRequest);

		return tokenResponse(authTokens);
	}

	@PostMapping("/auth/refresh")
	@Operation(summary = "Renovar sessão autenticada")
	@ApiResponse(responseCode = "200", description = "Sessão renovada")
	@ApiResponse(responseCode = "401", description = "Refresh token inválido")
	public ResponseEntity<AccessTokenResponseDTO> refresh(
		@CookieValue(name = "streamora_refresh_token", required = false) String refreshToken
	)
	{
		try {
			AuthTokens authTokens = authService.refresh(refreshToken);

			return tokenResponse(authTokens);
		} catch (org.springframework.web.server.ResponseStatusException exception) {
			return ResponseEntity.status(exception.getStatusCode())
				.header(HttpHeaders.SET_COOKIE, authCookieProperties.expire().toString())
				.build();
		}
	}

	@PostMapping("/auth/logout")
	@Operation(summary = "Encerrar sessão autenticada")
	@ApiResponse(responseCode = "204", description = "Sessão encerrada")
	public ResponseEntity<Void> logout() {
		return ResponseEntity.noContent()
			.header(HttpHeaders.SET_COOKIE, authCookieProperties.expire().toString())
			.build();
	}

	private ResponseEntity<AccessTokenResponseDTO> tokenResponse(AuthTokens authTokens) {
		return ResponseEntity.ok()
			.header(
				HttpHeaders.SET_COOKIE,
				authCookieProperties.create(authTokens.refreshToken()).toString()
			)
			.body(new AccessTokenResponseDTO(authTokens.accessToken()));
	}
}
