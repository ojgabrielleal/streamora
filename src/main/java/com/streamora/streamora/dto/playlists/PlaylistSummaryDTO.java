package com.streamora.streamora.dto.playlists;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaylistSummaryDTO(
	@Schema(example = "550e8400-e29b-41d4-a716-446655440000")
	String uuid,
	@Schema(example = "Programação da manhã")
	String name
) {}
