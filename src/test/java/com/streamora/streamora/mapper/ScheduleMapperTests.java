package com.streamora.streamora.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.dto.schedules.ScheduleRequestDTO;
import com.streamora.streamora.dto.schedules.ScheduleResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.Schedule;
import com.streamora.streamora.factory.PlaylistFactory;
import com.streamora.streamora.factory.ScheduleFactory;

class ScheduleMapperTests {

	@Test
	void mapsScheduleWithPlaylistSummary() {
		Schedule schedule = ScheduleFactory.toEntity();

		ScheduleResponseDTO scheduleResponse = ScheduleMapper.toResponse(schedule);

		assertThat(scheduleResponse.uuid()).isEqualTo(schedule.getUuid());
		assertThat(scheduleResponse.playlist().uuid()).isEqualTo(schedule.getPlaylist().getUuid());
		assertThat(scheduleResponse.playlist().name()).isEqualTo(schedule.getPlaylist().getName());
		assertThat(scheduleResponse.dayOfWeek()).isEqualTo(schedule.getDayOfWeek());
		assertThat(scheduleResponse.startTime()).isEqualTo(schedule.getStartTime());
		assertThat(scheduleResponse.endTime()).isEqualTo(schedule.getEndTime());
	}

	@Test
	void mapsRequestToScheduleAndPlaylistRelationship() {
		ScheduleRequestDTO scheduleRequest = ScheduleFactory.toRequest();
		Playlist playlist = PlaylistFactory.toEntity();

		Schedule schedule = ScheduleMapper.toEntity(scheduleRequest, playlist);

		assertThat(schedule.getPlaylist())
			.isSameAs(playlist);
		assertThat(schedule.getDayOfWeek()).isEqualTo(scheduleRequest.dayOfWeek());
		assertThat(schedule.getStartTime()).isEqualTo(scheduleRequest.startTime());
		assertThat(schedule.getEndTime()).isEqualTo(scheduleRequest.endTime());
	}
}
