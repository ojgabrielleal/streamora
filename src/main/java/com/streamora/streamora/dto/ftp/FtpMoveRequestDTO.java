package com.streamora.streamora.dto.ftp;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para mover um arquivo no storage FTP")
public record FtpMoveRequestDTO(
	@Schema(
		description = "Caminho relativo atual do arquivo",
		example = "entrada.mp3",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String path,
	@Schema(
		description = "Diretório de destino relativo; vazio representa a raiz permitida",
		example = "programas",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String targetDirectory
) {}
