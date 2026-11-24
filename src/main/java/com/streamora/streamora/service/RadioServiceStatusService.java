package com.streamora.streamora.service;

import java.util.function.BooleanSupplier;

import org.springframework.stereotype.Service;

import com.streamora.streamora.dto.services.RadioTelemetryResponseDTO;
import com.streamora.streamora.dto.services.ServiceAvailabilityStatus;
import com.streamora.streamora.dto.services.ServiceStatusResponseDTO;
import com.streamora.streamora.infrastructure.icecast.IcecastAvailabilityChecker;
import com.streamora.streamora.infrastructure.liquidsoap.interfaces.LiquidsoapProcessControl;
import com.streamora.streamora.infrastructure.liquidsoap.records.RadioTracksTelemetry;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RadioServiceStatusService {

	private final IcecastAvailabilityChecker icecastAvailabilityChecker;
	private final LiquidsoapProcessControl liquidsoapProcessControl;

	public ServiceStatusResponseDTO getStatus() {
		ServiceAvailabilityStatus icecastStatus = status(icecastAvailabilityChecker::isAvailable);
		ServiceAvailabilityStatus liquidsoapStatus = status(liquidsoapProcessControl::isAvailable);

		return new ServiceStatusResponseDTO(icecastStatus, liquidsoapStatus);
	}

	public RadioTelemetryResponseDTO getTelemetry() {
		Integer listeners = icecastAvailabilityChecker.getListenersCount().orElse(null);
		RadioTracksTelemetry tracks = liquidsoapProcessControl.getTracksTelemetry();

		return new RadioTelemetryResponseDTO(
			listeners,
			tracks.currentTrack(),
			tracks.nextTrack()
		);
	}

	private ServiceAvailabilityStatus status(BooleanSupplier availabilityChecker) {
		try {
			return availabilityChecker.getAsBoolean()
				? ServiceAvailabilityStatus.ONLINE
				: ServiceAvailabilityStatus.OFFLINE;
		} catch (RuntimeException exception) {
			return ServiceAvailabilityStatus.OFFLINE;
		}
	}
}
