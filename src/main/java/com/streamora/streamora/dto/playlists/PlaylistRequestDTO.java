package com.streamora.streamora.dto.playlists;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criar ou atualizar uma playlist")
public record PlaylistRequestDTO(
	@Schema(description = "Nome da playlist", example = "Programação da manhã")
	String name,
	@Schema(description = "Nome do arquivo M3U associado, sem caminho", example = "programacao-manha.m3u", nullable = true)
	String m3uFileName
) {}
