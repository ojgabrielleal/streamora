package com.streamora.streamora.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.dto.PlaylistRequestDTO;
import com.streamora.streamora.dto.PlaylistResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistFile;

class PlaylistMapperTests {

	@Test
	void mapsEntityToResponseWithFileUuids() {
		Playlist playlist = new Playlist();
		playlist.setId(1L);
		playlist.setUuid("playlist-uuid");
		playlist.setName("Playlist");
		PlaylistFile firstFile = file("first-file-uuid");
		PlaylistFile secondFile = file("second-file-uuid");
		playlist.getFiles().add(firstFile);
		playlist.getFiles().add(secondFile);

		PlaylistResponseDTO response = PlaylistMapper.toResponse(playlist);

		assertThat(response.uuid()).isEqualTo("playlist-uuid");
		assertThat(response.name()).isEqualTo("Playlist");
		assertThat(response.fileUuids())
				.containsExactlyInAnyOrder("first-file-uuid", "second-file-uuid");
		assertThat(PlaylistResponseDTO.class.getRecordComponents())
				.extracting(component -> component.getName())
				.doesNotContain("id");
	}

	@Test
	void mapsRequestToNewEntity() {
		Playlist playlist = PlaylistMapper.toEntity(new PlaylistRequestDTO("Playlist"));

		assertThat(playlist.getId()).isNull();
		assertThat(playlist.getUuid()).isNull();
		assertThat(playlist.getName()).isEqualTo("Playlist");
		assertThat(playlist.getFiles()).isEmpty();
	}

	private PlaylistFile file(String uuid) {
		PlaylistFile file = new PlaylistFile();
		file.setUuid(uuid);
		return file;
	}
}
