package com.streamora.streamora.dto.services;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Metadados de uma faixa em reprodução ou na fila")
public record TrackTelemetryDTO(
	@Schema(example = "Bohemian Rhapsody")
	String title,
	@Schema(example = "Queen", nullable = true)
	String artist,
	@Schema(nullable = true)
	String coverUrl
) {

	public TrackTelemetryDTO(String title, String artist) {
		this(title, artist, null);
	}
}
