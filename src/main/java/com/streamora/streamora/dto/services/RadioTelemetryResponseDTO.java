package com.streamora.streamora.dto.services;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Telemetria em tempo real da rádio")
public record RadioTelemetryResponseDTO(
	@Schema(example = "12", nullable = true)
	Integer listeners,
	TrackTelemetryDTO currentTrack,
	TrackTelemetryDTO nextTrack
) {}
