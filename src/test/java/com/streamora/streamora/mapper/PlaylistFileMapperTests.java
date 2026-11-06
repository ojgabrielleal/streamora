package com.streamora.streamora.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.dto.PlaylistFileRequestDTO;
import com.streamora.streamora.dto.PlaylistFileResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistFile;
import com.streamora.streamora.factory.PlaylistFactory;
import com.streamora.streamora.factory.PlaylistFileFactory;

class PlaylistFileMapperTests {

	@Test
	void mapsEntityToResponseWithPlaylistUuids() {
		PlaylistFile playlistFile = PlaylistFileFactory.toEntity();
		List<Playlist> playlists = PlaylistFactory.toEntity(2);

		playlistFile.getPlaylists().addAll(playlists);

		PlaylistFileResponseDTO response = PlaylistFileMapper.toResponse(playlistFile);

		assertThat(response.uuid()).isEqualTo(playlistFile.getUuid());
		assertThat(response.type()).isEqualTo(playlistFile.getType());
		assertThat(response.name()).isEqualTo(playlistFile.getName());
		assertThat(response.path()).isEqualTo(playlistFile.getPath());
		assertThat(response.playlistUuids())
			.containsExactlyInAnyOrder(playlists.getFirst().getUuid(), playlists.getLast().getUuid());
		assertThat(PlaylistFileResponseDTO.class.getRecordComponents())
			.extracting(component -> component.getName())
			.doesNotContain("id");
	}

	@Test
	void mapsRequestToNewEntity() {
		PlaylistFileRequestDTO request = PlaylistFileFactory.toCreate();

		PlaylistFile playlistFile = PlaylistFileMapper.toEntity(request);

		assertThat(playlistFile.getId()).isNull();
		assertThat(playlistFile.getUuid()).isNull();
		assertThat(playlistFile.getType()).isEqualTo(request.type());
		assertThat(playlistFile.getName()).isEqualTo(request.name());
		assertThat(playlistFile.getPath()).isEqualTo(request.path());
		assertThat(playlistFile.getPlaylists()).isEmpty();
	}

	@Test
	void mapsRequestToExistingEntityAndPreservesIdentityAndPlaylists() {
		PlaylistFile playlistFile = PlaylistFileFactory.toEntity();
		Playlist playlist = PlaylistFactory.toEntity();

		PlaylistFileRequestDTO request = PlaylistFileFactory.toCreate(2).getLast();
		playlistFile.getPlaylists().add(playlist);
		Long originalId = playlistFile.getId();
		String originalUuid = playlistFile.getUuid();

		PlaylistFile mappedFile = PlaylistFileMapper.toEntity(request, playlistFile);

		assertThat(mappedFile).isSameAs(playlistFile);
		assertThat(mappedFile.getId()).isEqualTo(originalId);
		assertThat(mappedFile.getUuid()).isEqualTo(originalUuid);
		assertThat(mappedFile.getType()).isEqualTo(request.type());
		assertThat(mappedFile.getName()).isEqualTo(request.name());
		assertThat(mappedFile.getPath()).isEqualTo(request.path());
		assertThat(mappedFile.getPlaylists()).containsExactly(playlist);
	}
}
