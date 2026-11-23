package com.streamora.streamora.dto.users;

import com.streamora.streamora.entity.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criar ou atualizar um usuário")
public record UserRequestDTO(
	@Schema(description = "Nome completo do usuário", example = "João Silva")
	String fullname,
	@Schema(description = "Nome de exibição", example = "João")
	String nickname,
	@Schema(
		description = "Nome imutável usado para autenticação; opcional na atualização",
		example = "joao.silva",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String username,
	@Schema(
		description = "Senha do usuário; opcional na atualização",
		example = "senha-segura",
		accessMode = Schema.AccessMode.WRITE_ONLY,
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	String password,
	@Schema(
		description = "Nível de permissão do usuário; opcional",
		example = "STANDARD",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	UserRole role
) {
	public UserRequestDTO(
		String fullname,
		String nickname,
		String username,
		String password
	)
	{
		this(fullname, nickname, username, password, null);
	}
}
