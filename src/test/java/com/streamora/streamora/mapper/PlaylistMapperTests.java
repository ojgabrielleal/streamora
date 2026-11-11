package com.streamora.streamora.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.dto.PlaylistRequestDTO;
import com.streamora.streamora.dto.PlaylistResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistFile;
import com.streamora.streamora.entity.PlaylistSchedule;
import com.streamora.streamora.factory.PlaylistFactory;
import com.streamora.streamora.factory.PlaylistFileFactory;
import com.streamora.streamora.factory.PlaylistScheduleFactory;

class PlaylistMapperTests {

	@Test
	void mapsEntityToResponseWithFileUuids() {
		Playlist playlist = PlaylistFactory.toEntity();
		List<PlaylistFile> files = PlaylistFileFactory.toEntity(2);

		playlist.getFiles().addAll(files);

		PlaylistResponseDTO response = PlaylistMapper.toResponse(playlist);

		assertThat(response.uuid()).isEqualTo(playlist.getUuid());
		assertThat(response.name()).isEqualTo(playlist.getName());
		assertThat(response.fileUuids())
			.containsExactlyInAnyOrder(files.getFirst().getUuid(), files.getLast().getUuid());
		assertThat(PlaylistResponseDTO.class.getRecordComponents())
			.extracting(component -> component.getName())
			.doesNotContain("id");
	}

	@Test
	void mapsPlaylistSchedulesWithoutNestedPlaylist() {
		Playlist playlist = PlaylistFactory.toEntity();
		PlaylistSchedule schedule = PlaylistScheduleFactory.toEntity();
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
		assertThat(playlist.getFiles()).isEmpty();
	}

	@Test
	void mapsRequestToExistingEntityAndPreservesIdentityAndFiles() {
		Playlist playlist = PlaylistFactory.toEntity();
		PlaylistFile playlistFile = PlaylistFileFactory.toEntity();

		PlaylistRequestDTO request = PlaylistFactory.toCreate(2).getLast();
		playlist.getFiles().add(playlistFile);
		Long originalId = playlist.getId();
		String originalUuid = playlist.getUuid();

		Playlist mappedPlaylist = PlaylistMapper.toEntity(request, playlist);

		assertThat(mappedPlaylist).isSameAs(playlist);
		assertThat(mappedPlaylist.getId()).isEqualTo(originalId);
		assertThat(mappedPlaylist.getUuid()).isEqualTo(originalUuid);
		assertThat(mappedPlaylist.getName()).isEqualTo(request.name());
		assertThat(mappedPlaylist.getFiles()).containsExactly(playlistFile);
	}
}
