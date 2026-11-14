package com.streamora.streamora.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciais para autenticação")
public record AuthRequestDTO(
	@Schema(
		description = "Nome usado para autenticação",
		example = "joao.silva",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String username,
	@Schema(
		description = "Senha do usuário",
		example = "senha-segura",
		accessMode = Schema.AccessMode.WRITE_ONLY,
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String password
) {}
