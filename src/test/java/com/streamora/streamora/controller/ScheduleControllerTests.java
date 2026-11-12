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
		ScheduleResponseDTO scheduleResponse = ScheduleMapper.toResponse(ScheduleFactory.toEntity());

		when(scheduleService.index())
			.thenReturn(List.of(scheduleResponse));

		List<ScheduleResponseDTO> returnedSchedules = scheduleController.index();

		assertThat(returnedSchedules)
			.containsExactly(scheduleResponse);

		verify(scheduleService).index();
	}

	@Test
	void delegatesShowToService() {
		ScheduleResponseDTO scheduleResponse = ScheduleMapper.toResponse(ScheduleFactory.toEntity());

		when(scheduleService.show("schedule-uuid-1"))
			.thenReturn(scheduleResponse);

		ScheduleResponseDTO returnedSchedule = scheduleController.show("schedule-uuid-1");

		assertThat(returnedSchedule)
			.isSameAs(scheduleResponse);

		verify(scheduleService).show("schedule-uuid-1");
	}

	@Test
	void delegatesStoreToService() {
		ScheduleRequestDTO scheduleRequest = ScheduleFactory.toRequest();
		ScheduleResponseDTO scheduleResponse = ScheduleMapper.toResponse(ScheduleFactory.toEntity());

		when(scheduleService.store(scheduleRequest))
			.thenReturn(scheduleResponse);

		ScheduleResponseDTO createdSchedule = scheduleController.store(scheduleRequest);

		assertThat(createdSchedule)
			.isSameAs(scheduleResponse);

		verify(scheduleService).store(scheduleRequest);
	}

	@Test
	void delegatesUpdateToService() {
		ScheduleRequestDTO scheduleRequest = ScheduleFactory.toRequest();
		ScheduleResponseDTO scheduleResponse = ScheduleMapper.toResponse(ScheduleFactory.toEntity());

		when(scheduleService.update("schedule-uuid-1", scheduleRequest))
			.thenReturn(scheduleResponse);

		ScheduleResponseDTO updatedSchedule = scheduleController.update("schedule-uuid-1", scheduleRequest);

		assertThat(updatedSchedule)
			.isSameAs(scheduleResponse);

		verify(scheduleService).update("schedule-uuid-1", scheduleRequest);
	}

	@Test
	void delegatesDeleteToService() {
		scheduleController.delete("schedule-uuid-1");

		verify(scheduleService).delete("schedule-uuid-1");
	}
}
