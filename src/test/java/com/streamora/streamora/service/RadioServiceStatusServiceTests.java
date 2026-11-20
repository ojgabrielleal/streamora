package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.streamora.streamora.dto.services.ServiceAvailabilityStatus;
import com.streamora.streamora.dto.services.ServiceStatusResponseDTO;
import com.streamora.streamora.infrastructure.icecast.IcecastAvailabilityChecker;
import com.streamora.streamora.infrastructure.liquidsoap.interfaces.LiquidsoapProcessControl;

@ExtendWith(MockitoExtension.class)
class RadioServiceStatusServiceTests {

	@Mock
	private IcecastAvailabilityChecker icecastAvailabilityChecker;

	@Mock
	private LiquidsoapProcessControl liquidsoapProcessControl;

	@InjectMocks
	private RadioServiceStatusService radioServiceStatusService;

	@Test
	void reportsBothServicesOnline() {
		when(icecastAvailabilityChecker.isAvailable())
			.thenReturn(true);
		when(liquidsoapProcessControl.isAvailable())
			.thenReturn(true);

		ServiceStatusResponseDTO serviceStatus = radioServiceStatusService.getStatus();

		assertThat(serviceStatus.icecast()).isEqualTo(ServiceAvailabilityStatus.ONLINE);
		assertThat(serviceStatus.liquidsoap()).isEqualTo(ServiceAvailabilityStatus.ONLINE);
	}

	@Test
	void reportsIndependentServiceStatuses() {
		when(icecastAvailabilityChecker.isAvailable())
			.thenReturn(true);
		when(liquidsoapProcessControl.isAvailable())
			.thenReturn(false);

		ServiceStatusResponseDTO serviceStatus = radioServiceStatusService.getStatus();

		assertThat(serviceStatus.icecast()).isEqualTo(ServiceAvailabilityStatus.ONLINE);
		assertThat(serviceStatus.liquidsoap()).isEqualTo(ServiceAvailabilityStatus.OFFLINE);
	}

	@Test
	void isolatesUnexpectedCheckerFailure() {
		when(icecastAvailabilityChecker.isAvailable())
			.thenThrow(new IllegalStateException("failure"));
		when(liquidsoapProcessControl.isAvailable())
			.thenReturn(true);

		ServiceStatusResponseDTO serviceStatus = radioServiceStatusService.getStatus();

		assertThat(serviceStatus.icecast()).isEqualTo(ServiceAvailabilityStatus.OFFLINE);
		assertThat(serviceStatus.liquidsoap()).isEqualTo(ServiceAvailabilityStatus.ONLINE);
	}
}
