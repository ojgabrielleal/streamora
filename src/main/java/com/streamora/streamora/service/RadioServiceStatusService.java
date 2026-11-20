package com.streamora.streamora.service;

import java.util.function.BooleanSupplier;

import org.springframework.stereotype.Service;

import com.streamora.streamora.dto.services.ServiceAvailabilityStatus;
import com.streamora.streamora.dto.services.ServiceStatusResponseDTO;
import com.streamora.streamora.infrastructure.icecast.IcecastAvailabilityChecker;
import com.streamora.streamora.infrastructure.liquidsoap.interfaces.LiquidsoapProcessControl;

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
