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

		PlaylistResponseDTO playlistResponse = PlaylistMapper.toResponse(playlist);

		assertThat(playlistResponse.uuid()).isEqualTo(playlist.getUuid());
		assertThat(playlistResponse.name()).isEqualTo(playlist.getName());
		assertThat(playlistResponse.m3uFileName()).isEqualTo(playlist.getM3uFileName());
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

		PlaylistResponseDTO playlistResponse = PlaylistMapper.toResponse(playlist);

		assertThat(playlistResponse.schedules()).singleElement().satisfies(scheduleResponse -> {
			assertThat(scheduleResponse.uuid()).isEqualTo(schedule.getUuid());
			assertThat(scheduleResponse.dayOfWeek()).isEqualTo(schedule.getDayOfWeek());
		});
	}

	@Test
	void mapsRequestToNewEntity() {
		PlaylistRequestDTO playlistCreateRequest = PlaylistFactory.toCreate();

		Playlist playlist = PlaylistMapper.toEntity(playlistCreateRequest);

		assertThat(playlist.getId()).isNull();
		assertThat(playlist.getUuid()).isNull();
		assertThat(playlist.getName()).isEqualTo(playlistCreateRequest.name());
		assertThat(playlist.getM3uFileName()).isEqualTo(playlistCreateRequest.m3uFileName());
	}

	@Test
	void mapsRequestToExistingEntityAndPreservesIdentity() {
		Playlist playlist = PlaylistFactory.toEntity();

		PlaylistRequestDTO playlistUpdateRequest = PlaylistFactory.toCreate(2).getLast();
		Long originalId = playlist.getId();
		String originalUuid = playlist.getUuid();

		Playlist mappedPlaylist = PlaylistMapper.toEntity(playlistUpdateRequest, playlist);

		assertThat(mappedPlaylist)
			.isSameAs(playlist);
		assertThat(mappedPlaylist.getId()).isEqualTo(originalId);
		assertThat(mappedPlaylist.getUuid()).isEqualTo(originalUuid);
		assertThat(mappedPlaylist.getName()).isEqualTo(playlistUpdateRequest.name());
		assertThat(mappedPlaylist.getM3uFileName()).isEqualTo(playlistUpdateRequest.m3uFileName());
	}

	@Test
	void allowsM3uFileNameToRemainEmpty() {
		Playlist playlist = PlaylistMapper.toEntity(new PlaylistRequestDTO("Playlist", null));

		assertThat(playlist.getM3uFileName()).isNull();
	}

	@Test
	void rejectsM3uFileNameWithWindowsDirectoryPath() {
		PlaylistRequestDTO invalidPlaylistRequest = new PlaylistRequestDTO("Playlist", "directory\\playlist.m3u");

		org.assertj.core.api.Assertions.assertThatThrownBy(() -> PlaylistMapper.toEntity(invalidPlaylistRequest))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("M3U file name must not contain a directory path");
	}
}
