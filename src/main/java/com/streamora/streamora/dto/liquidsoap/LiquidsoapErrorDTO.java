package com.streamora.streamora.dto.liquidsoap;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Falha ao aplicar a configuração do Liquidsoap")
public record LiquidsoapErrorDTO(
	@Schema(
		description = "Mensagem segura que descreve a falha da operação",
		example = "Liquidsoap control is unavailable",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String message
) {}
