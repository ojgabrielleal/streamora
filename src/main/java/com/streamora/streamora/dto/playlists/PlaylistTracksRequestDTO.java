package com.streamora.streamora.dto.playlists;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Caminhos das faixas que substituirão o conteúdo da playlist")
public record PlaylistTracksRequestDTO(
	@Schema(
		description = "Caminhos relativos das mídias no storage compartilhado",
		example = "[\"musicas/faixa-1.mp3\", \"musicas/faixa-2.flac\"]",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	List<String> paths
) {}
