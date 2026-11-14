package com.streamora.streamora.dto.ftp;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para renomear um item do storage FTP")
public record FtpRenameRequestDTO(
	@Schema(
		description = "Caminho relativo atual do item",
		example = "programas/abertura.mp3",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String path,
	@Schema(
		description = "Novo nome do item, sem componentes de diretório",
		example = "abertura-nova.mp3",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String newName
) {}
