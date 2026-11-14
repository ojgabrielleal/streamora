package com.streamora.streamora.dto.users;

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
	String password
) {}
