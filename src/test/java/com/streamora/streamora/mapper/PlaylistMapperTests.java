package com.streamora.streamora.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.dto.PlaylistRequestDTO;
import com.streamora.streamora.dto.PlaylistResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.Schedule;
import com.streamora.streamora.factory.PlaylistFactory;
import com.streamora.streamora.factory.ScheduleFactory;

class PlaylistMapperTests {

	@Test
	void mapsEntityToResponseWithM3uFileName() {
		Playlist playlist = PlaylistFactory.toEntity();

		PlaylistResponseDTO response = PlaylistMapper.toResponse(playlist);

		assertThat(response.uuid()).isEqualTo(playlist.getUuid());
		assertThat(response.name()).isEqualTo(playlist.getName());
		assertThat(response.m3uFileName()).isEqualTo(playlist.getM3uFileName());
		assertThat(PlaylistResponseDTO.class.getRecordComponents())
			.extracting(component -> component.getName())
			.doesNotContain("id");
	}

	@Test
	void mapsSchedulesWithoutNestedPlaylist() {
		Playlist playlist = PlaylistFactory.toEntity();
		Schedule schedule = ScheduleFactory.toEntity();
		schedule.setPlaylist(playlist);
		playlist.getSchedules().add(schedule);

		PlaylistResponseDTO response = PlaylistMapper.toResponse(playlist);

		assertThat(response.schedules()).singleElement().satisfies(scheduleResponse -> {
			assertThat(scheduleResponse.uuid()).isEqualTo(schedule.getUuid());
			assertThat(scheduleResponse.dayOfWeek()).isEqualTo(schedule.getDayOfWeek());
		});
	}

	@Test
	void mapsRequestToNewEntity() {
		PlaylistRequestDTO request = PlaylistFactory.toCreate();

		Playlist playlist = PlaylistMapper.toEntity(request);

		assertThat(playlist.getId()).isNull();
		assertThat(playlist.getUuid()).isNull();
		assertThat(playlist.getName()).isEqualTo(request.name());
		assertThat(playlist.getM3uFileName()).isEqualTo(request.m3uFileName());
	}

	@Test
	void mapsRequestToExistingEntityAndPreservesIdentity() {
		Playlist playlist = PlaylistFactory.toEntity();

		PlaylistRequestDTO request = PlaylistFactory.toCreate(2).getLast();
		Long originalId = playlist.getId();
		String originalUuid = playlist.getUuid();

		Playlist mappedPlaylist = PlaylistMapper.toEntity(request, playlist);

		assertThat(mappedPlaylist)
			.isSameAs(playlist);
		assertThat(mappedPlaylist.getId()).isEqualTo(originalId);
		assertThat(mappedPlaylist.getUuid()).isEqualTo(originalUuid);
		assertThat(mappedPlaylist.getName()).isEqualTo(request.name());
		assertThat(mappedPlaylist.getM3uFileName()).isEqualTo(request.m3uFileName());
	}

	@Test
	void allowsM3uFileNameToRemainEmpty() {
		Playlist playlist = PlaylistMapper.toEntity(new PlaylistRequestDTO("Playlist", null));

		assertThat(playlist.getM3uFileName()).isNull();
	}

	@Test
	void rejectsM3uFileNameWithWindowsDirectoryPath() {
		PlaylistRequestDTO request = new PlaylistRequestDTO("Playlist", "directory\\playlist.m3u");

		org.assertj.core.api.Assertions.assertThatThrownBy(() -> PlaylistMapper.toEntity(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("M3U file name must not contain a directory path");
	}
}
