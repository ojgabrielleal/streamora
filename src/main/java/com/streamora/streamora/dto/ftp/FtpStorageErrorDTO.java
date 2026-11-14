package com.streamora.streamora.dto.ftp;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Falha ao consultar ou manipular o storage FTP")
public record FtpStorageErrorDTO(
	@Schema(
		description = "Mensagem segura que descreve a falha da operação",
		example = "FTP destination already exists",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String message
) {}
