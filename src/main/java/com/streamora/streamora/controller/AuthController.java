package com.streamora.streamora.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.streamora.streamora.dto.AuthRequestDTO;
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

	@PostMapping("/login")
	@Operation(summary = "Autenticar usuário")
	@ApiResponse(responseCode = "200", description = "Usuário autenticado")
	@ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content(schema = @Schema(hidden = true)))
	public Map<String, String> auth(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Credenciais do usuário", required = true)
		@RequestBody AuthRequestDTO authRequest
	) {
		return authService.auth(authRequest);
	}
}
