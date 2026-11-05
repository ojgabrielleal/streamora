package com.streamora.streamora.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.dto.PlaylistFileRequestDTO;
import com.streamora.streamora.dto.PlaylistFileResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistFile;
import com.streamora.streamora.entity.type.PlaylistFileType;

class PlaylistFileMapperTests {

	@Test
	void mapsEntityToResponseWithPlaylistUuids() {
		PlaylistFile playlistFile = new PlaylistFile();
		playlistFile.setId(1L);
		playlistFile.setUuid("file-uuid");
		playlistFile.setType(PlaylistFileType.music);
		playlistFile.setName("Audio");
		playlistFile.setPath("/audio/file.mp3");
		Playlist firstPlaylist = playlist("first-playlist-uuid");
		Playlist secondPlaylist = playlist("second-playlist-uuid");
		playlistFile.getPlaylists().add(firstPlaylist);
		playlistFile.getPlaylists().add(secondPlaylist);

		PlaylistFileResponseDTO response = PlaylistFileMapper.toResponse(playlistFile);

		assertThat(response.uuid()).isEqualTo("file-uuid");
		assertThat(response.type()).isEqualTo(PlaylistFileType.music);
		assertThat(response.name()).isEqualTo("Audio");
		assertThat(response.path()).isEqualTo("/audio/file.mp3");
		assertThat(response.playlistUuids())
				.containsExactlyInAnyOrder("first-playlist-uuid", "second-playlist-uuid");
		assertThat(PlaylistFileResponseDTO.class.getRecordComponents())
				.extracting(component -> component.getName())
				.doesNotContain("id");
	}

	@Test
	void mapsRequestToNewEntity() {
		PlaylistFileRequestDTO request = new PlaylistFileRequestDTO(
				PlaylistFileType.music,
				"Audio",
				"/audio/file.mp3"
		);

		PlaylistFile playlistFile = PlaylistFileMapper.toEntity(request);

		assertThat(playlistFile.getId()).isNull();
		assertThat(playlistFile.getUuid()).isNull();
		assertThat(playlistFile.getType()).isEqualTo(PlaylistFileType.music);
		assertThat(playlistFile.getName()).isEqualTo("Audio");
		assertThat(playlistFile.getPath()).isEqualTo("/audio/file.mp3");
		assertThat(playlistFile.getPlaylists()).isEmpty();
	}

	private Playlist playlist(String uuid) {
		Playlist playlist = new Playlist();
		playlist.setUuid(uuid);
		return playlist;
	}
}
