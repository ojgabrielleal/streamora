package com.streamora.streamora.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.streamora.streamora.dto.users.UserRequestDTO;
import com.streamora.streamora.dto.users.UserResponseDTO;
import com.streamora.streamora.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UserController {

	private final UserService userService;

	@GetMapping
	@Operation(summary = "Listar usuários")
	@ApiResponse(
		responseCode = "200",
		description = "Usuários encontrados",
		content = @Content(
			array = @ArraySchema(
				schema = @Schema(implementation = UserResponseDTO.class)
			)
		)
	)
	public List<UserResponseDTO> index() {
		return userService.index();
	}

	@GetMapping("/{uuid}")
	@Operation(summary = "Consultar usuário")
	@ApiResponse(responseCode = "200", description = "Usuário encontrado")
	public UserResponseDTO show(
		@Parameter(description = "UUID do usuário", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable String uuid
	)
	{
		return userService.show(uuid);
	}

	@PostMapping
	@Operation(summary = "Criar usuário")
	@ApiResponse(responseCode = "200", description = "Usuário criado")
	public UserResponseDTO store(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do usuário", required = true)
		@RequestBody UserRequestDTO userRequest
	)
	{
		return userService.store(userRequest);
	}

	@PutMapping("/{uuid}")
	@Operation(summary = "Atualizar usuário")
	@ApiResponse(responseCode = "200", description = "Usuário atualizado")
	public UserResponseDTO update(
		@Parameter(description = "UUID do usuário", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable String uuid,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados do usuário", required = true)
		@RequestBody UserRequestDTO userRequest
	)
	{
		return userService.update(uuid, userRequest);
	}

	@DeleteMapping("/{uuid}")
	@Operation(summary = "Excluir usuário")
	@ApiResponse(responseCode = "200", description = "Usuário excluído")
	public void delete(
		@Parameter(description = "UUID do usuário", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable String uuid
	)
	{
		userService.delete(uuid);
	}
}
