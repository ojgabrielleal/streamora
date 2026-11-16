package com.streamora.streamora.dto.playlists;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Falha ao consultar ou alterar uma playlist")
public record PlaylistErrorDTO(
	@Schema(
		description = "Mensagem segura que descreve a falha da operação",
		example = "Associated M3U file is invalid",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	String message
) {}
