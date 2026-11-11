package com.streamora.streamora.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.dto.PlaylistScheduleRequestDTO;
import com.streamora.streamora.dto.PlaylistScheduleResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistSchedule;
import com.streamora.streamora.factory.PlaylistFactory;
import com.streamora.streamora.factory.PlaylistScheduleFactory;

class PlaylistScheduleMapperTests {

	@Test
	void mapsScheduleWithPlaylistSummary() {
		PlaylistSchedule schedule = PlaylistScheduleFactory.toEntity();

		PlaylistScheduleResponseDTO response = PlaylistScheduleMapper.toResponse(schedule);

		assertThat(response.uuid()).isEqualTo(schedule.getUuid());
		assertThat(response.playlist().uuid()).isEqualTo(schedule.getPlaylist().getUuid());
		assertThat(response.playlist().name()).isEqualTo(schedule.getPlaylist().getName());
		assertThat(response.dayOfWeek()).isEqualTo(schedule.getDayOfWeek());
		assertThat(response.startTime()).isEqualTo(schedule.getStartTime());
		assertThat(response.endTime()).isEqualTo(schedule.getEndTime());
	}

	@Test
	void mapsRequestToScheduleAndPlaylistRelationship() {
		PlaylistScheduleRequestDTO request = PlaylistScheduleFactory.toRequest();
		Playlist playlist = PlaylistFactory.toEntity();

		PlaylistSchedule schedule = PlaylistScheduleMapper.toEntity(request, playlist);

		assertThat(schedule.getPlaylist()).isSameAs(playlist);
		assertThat(schedule.getDayOfWeek()).isEqualTo(request.dayOfWeek());
		assertThat(schedule.getStartTime()).isEqualTo(request.startTime());
		assertThat(schedule.getEndTime()).isEqualTo(request.endTime());
	}
}
