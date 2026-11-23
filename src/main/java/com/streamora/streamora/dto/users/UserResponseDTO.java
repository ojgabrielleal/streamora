package com.streamora.streamora.dto.users;

import com.streamora.streamora.entity.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Usuário retornado pela API")
public record UserResponseDTO(
	@Schema(description = "Identificador público do usuário", example = "550e8400-e29b-41d4-a716-446655440000")
	String uuid,
	@Schema(description = "Nome completo do usuário", example = "João Silva")
	String fullname,
	@Schema(description = "Nome de exibição", example = "João")
	String nickname,
	@Schema(description = "Nome usado para autenticação", example = "joao.silva")
	String username,
	@Schema(description = "Nível de permissão do usuário", example = "STANDARD")
	UserRole role
) {
	public UserResponseDTO(
		String uuid,
		String fullname,
		String nickname,
		String username
	)
	{
		this(uuid, fullname, nickname, username, UserRole.STANDARD);
	}
}
