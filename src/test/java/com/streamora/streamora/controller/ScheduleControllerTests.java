package com.streamora.streamora.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.RequestMapping;

import com.streamora.streamora.dto.ScheduleRequestDTO;
import com.streamora.streamora.dto.ScheduleResponseDTO;
import com.streamora.streamora.factory.ScheduleFactory;
import com.streamora.streamora.mapper.ScheduleMapper;
import com.streamora.streamora.service.ScheduleService;

@ExtendWith(MockitoExtension.class)
class ScheduleControllerTests {

	@Mock
	private ScheduleService scheduleService;

	@InjectMocks
	private ScheduleController scheduleController;

	@Test
	void usesSchedulePrefix() {
		assertThat(ScheduleController.class.getAnnotation(RequestMapping.class).value())
			.containsExactly("/schedules");
	}

	@Test
	void delegatesIndexToService() {
		ScheduleResponseDTO response = ScheduleMapper.toResponse(ScheduleFactory.toEntity());

		when(scheduleService.index())
			.thenReturn(List.of(response));

		assertThat(scheduleController.index())
			.containsExactly(response);

		verify(scheduleService).index();
	}

	@Test
	void delegatesShowToService() {
		ScheduleResponseDTO response = ScheduleMapper.toResponse(ScheduleFactory.toEntity());

		when(scheduleService.show("schedule-uuid-1"))
			.thenReturn(response);

		assertThat(scheduleController.show("schedule-uuid-1"))
			.isSameAs(response);

		verify(scheduleService).show("schedule-uuid-1");
	}

	@Test
	void delegatesStoreToService() {
		ScheduleRequestDTO request = ScheduleFactory.toRequest();
		ScheduleResponseDTO response = ScheduleMapper.toResponse(ScheduleFactory.toEntity());

		when(scheduleService.store(request))
			.thenReturn(response);

		assertThat(scheduleController.store(request))
			.isSameAs(response);

		verify(scheduleService).store(request);
	}

	@Test
	void delegatesUpdateToService() {
		ScheduleRequestDTO request = ScheduleFactory.toRequest();
		ScheduleResponseDTO response = ScheduleMapper.toResponse(ScheduleFactory.toEntity());

		when(scheduleService.update("schedule-uuid-1", request))
			.thenReturn(response);

		assertThat(scheduleController.update("schedule-uuid-1", request))
			.isSameAs(response);

		verify(scheduleService).update("schedule-uuid-1", request);
	}

	@Test
	void delegatesDeleteToService() {
		scheduleController.delete("schedule-uuid-1");

		verify(scheduleService).delete("schedule-uuid-1");
	}
}
