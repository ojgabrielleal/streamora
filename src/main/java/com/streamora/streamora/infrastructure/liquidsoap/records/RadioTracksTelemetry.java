package com.streamora.streamora.infrastructure.liquidsoap.records;

import com.streamora.streamora.dto.services.TrackTelemetryDTO;

public record RadioTracksTelemetry(
	TrackTelemetryDTO currentTrack,
	TrackTelemetryDTO nextTrack
) {

	public static final RadioTracksTelemetry EMPTY = new RadioTracksTelemetry(null, null);
}
