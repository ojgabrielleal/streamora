package com.streamora.streamora.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.streamora.streamora.dto.liquidsoap.LiquidsoapAuthRequestDTO;
import com.streamora.streamora.dto.liquidsoap.LiquidsoapErrorDTO;
import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapApplyInProgressException;
import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapControlException;
import com.streamora.streamora.service.LiquidsoapAuthService;
import com.streamora.streamora.service.LiquidsoapService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/liquidsoap")
@RequiredArgsConstructor
@Tag(name = "Liquidsoap", description = "Aplicação da configuração de reprodução")
public class LiquidsoapController {

	private final LiquidsoapService liquidsoapService;
	private final LiquidsoapAuthService liquidsoapAuthService;

	@PostMapping("/auth")
	@Operation(summary = "Autenticar locutor no Liquidsoap")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Locutor autenticado"),
		@ApiResponse(responseCode = "401", description = "Credenciais inválidas")
	})
	public ResponseEntity<Void> authenticate(@Valid @RequestBody LiquidsoapAuthRequestDTO authRequest) {
		if (!liquidsoapAuthService.authenticate(authRequest.username(), authRequest.password())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		return ResponseEntity.ok().build();
	}

	@PostMapping("/apply")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Aplicar configuração atual no Liquidsoap")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Configuração aplicada"),
		@ApiResponse(responseCode = "401", description = "Autenticação necessária"),
		@ApiResponse(
			responseCode = "409",
			description = "Outra aplicação está em andamento",
			content = @Content(schema = @Schema(implementation = LiquidsoapErrorDTO.class))
		),
		@ApiResponse(
			responseCode = "503",
			description = "Liquidsoap indisponível",
			content = @Content(schema = @Schema(implementation = LiquidsoapErrorDTO.class))
		)
	})
	public void apply() {
		liquidsoapService.applyConfiguration();
	}

	@ExceptionHandler(LiquidsoapApplyInProgressException.class)
	public ResponseEntity<LiquidsoapErrorDTO> handleApplyInProgress(
		LiquidsoapApplyInProgressException exception
	)
	{
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new LiquidsoapErrorDTO(exception.getMessage()));
	}

	@ExceptionHandler(LiquidsoapControlException.class)
	public ResponseEntity<LiquidsoapErrorDTO> handleControlFailure(
		LiquidsoapControlException exception
	)
	{
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
			.body(new LiquidsoapErrorDTO(exception.getMessage()));
	}
}
