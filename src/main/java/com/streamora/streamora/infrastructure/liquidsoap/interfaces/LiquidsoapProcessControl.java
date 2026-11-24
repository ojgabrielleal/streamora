package com.streamora.streamora.infrastructure.liquidsoap.interfaces;

import com.streamora.streamora.infrastructure.liquidsoap.records.RadioTracksTelemetry;

public interface LiquidsoapProcessControl {

	boolean isAvailable();

	void restartAndAwaitRecovery();

	default RadioTracksTelemetry getTracksTelemetry() {
		return RadioTracksTelemetry.EMPTY;
	}
}
