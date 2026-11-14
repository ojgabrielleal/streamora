package com.streamora.streamora.dto.ftp;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação de um diretório no storage FTP")
public record FtpDirectoryCreateRequestDTO(
	@Schema(
		description = "Caminho relativo do novo diretório",
		example = "programas/jornal",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String path
) {}
