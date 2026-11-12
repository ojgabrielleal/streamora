package com.streamora.streamora.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.dto.ScheduleRequestDTO;
import com.streamora.streamora.dto.ScheduleResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.Schedule;
import com.streamora.streamora.factory.PlaylistFactory;
import com.streamora.streamora.factory.ScheduleFactory;

class ScheduleMapperTests {

	@Test
	void mapsScheduleWithPlaylistSummary() {
		Schedule schedule = ScheduleFactory.toEntity();

		ScheduleResponseDTO response = ScheduleMapper.toResponse(schedule);

		assertThat(response.uuid()).isEqualTo(schedule.getUuid());
		assertThat(response.playlist().uuid()).isEqualTo(schedule.getPlaylist().getUuid());
		assertThat(response.playlist().name()).isEqualTo(schedule.getPlaylist().getName());
		assertThat(response.dayOfWeek()).isEqualTo(schedule.getDayOfWeek());
		assertThat(response.startTime()).isEqualTo(schedule.getStartTime());
		assertThat(response.endTime()).isEqualTo(schedule.getEndTime());
	}

	@Test
	void mapsRequestToScheduleAndPlaylistRelationship() {
		ScheduleRequestDTO request = ScheduleFactory.toRequest();
		Playlist playlist = PlaylistFactory.toEntity();

		Schedule schedule = ScheduleMapper.toEntity(request, playlist);

		assertThat(schedule.getPlaylist())
			.isSameAs(playlist);
		assertThat(schedule.getDayOfWeek()).isEqualTo(request.dayOfWeek());
		assertThat(schedule.getStartTime()).isEqualTo(request.startTime());
		assertThat(schedule.getEndTime()).isEqualTo(request.endTime());
	}
}
