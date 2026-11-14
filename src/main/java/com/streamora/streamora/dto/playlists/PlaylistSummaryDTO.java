package com.streamora.streamora.dto.playlists;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaylistSummaryDTO(
	@Schema(example = "550e8400-e29b-41d4-a716-446655440000")
	String uuid,
	@Schema(example = "Programação da manhã")
	String name,
	@Schema(description = "Indica se a playlist participa do fallback base", example = "true")
	boolean isBase,
	@Schema(description = "Peso positivo da playlist nas combinações", example = "2", minimum = "1")
	int weight
) {}
