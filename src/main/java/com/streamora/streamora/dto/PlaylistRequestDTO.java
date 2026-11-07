package com.streamora.streamora.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criar ou atualizar uma playlist")
public record PlaylistRequestDTO(
	@Schema(description = "Nome da playlist", example = "Programação da manhã")
	String name
) {}
