package com.streamora.streamora.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criar ou atualizar um usuário")
public record UserRequestDTO(
	@Schema(description = "Nome completo do usuário", example = "João Silva")
	String fullname,
	@Schema(description = "Nome de exibição", example = "João")
	String nickname,
	@Schema(description = "Nome usado para autenticação", example = "joao.silva")
	String username,
	@Schema(description = "Senha do usuário", example = "senha-segura", accessMode = Schema.AccessMode.WRITE_ONLY)
	String password
) {}
