package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.streamora.streamora.dto.PlaylistRequestDTO;
import com.streamora.streamora.dto.PlaylistResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.factory.PlaylistFactory;
import com.streamora.streamora.repository.PlaylistRepository;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTests {

	@Mock
	private PlaylistRepository playlistRepository;

	@InjectMocks
	private PlaylistService playlistService;

	@Test
	void indexesPlaylists() {
		List<Playlist> playlistEntities = PlaylistFactory.toEntity(2);

		when(playlistRepository.findAll())
			.thenReturn(playlistEntities);

		List<PlaylistResponseDTO> indexedPlaylists = playlistService.index();

		assertThat(indexedPlaylists)
			.extracting(playlist -> playlist.uuid())
			.containsExactly(playlistEntities.getFirst().getUuid(), playlistEntities.getLast().getUuid());
	}

	@Test
	void showsPlaylistByUuid() {
		Playlist playlistEntity = PlaylistFactory.toEntity();

		when(playlistRepository.findByUuid(playlistEntity.getUuid()))
			.thenReturn(Optional.of(playlistEntity));

		PlaylistResponseDTO returnedPlaylist = playlistService.show(playlistEntity.getUuid());

		assertThat(returnedPlaylist.uuid()).isEqualTo(playlistEntity.getUuid());
	}

	@Test
	void storesPlaylist() {
		PlaylistRequestDTO playlistCreateRequest = PlaylistFactory.toCreate();

		when(playlistRepository.save(org.mockito.ArgumentMatchers.any(Playlist.class)))
			.thenAnswer(invocation -> {
				Playlist playlistToPersist = invocation.getArgument(0);
				assertThat(playlistToPersist.getUuid()).isNull();
				playlistToPersist.setUuid("generated-by-persistence");
				return playlistToPersist;
			});

		PlaylistResponseDTO createdPlaylist = playlistService.store(playlistCreateRequest);

		assertThat(createdPlaylist.uuid()).isEqualTo("generated-by-persistence");
		assertThat(createdPlaylist.name()).isEqualTo(playlistCreateRequest.name());
		assertThat(createdPlaylist.m3uFileName()).isEqualTo(playlistCreateRequest.m3uFileName());
	}

	@Test
	void updatesNameAndM3uFileNameAndPreservesIdentity() {
		Playlist storedPlaylist = PlaylistFactory.toEntity();

		PlaylistRequestDTO playlistUpdateRequest = PlaylistFactory.toCreate(2).getLast();
		Long originalId = storedPlaylist.getId();

		when(playlistRepository.findByUuid(storedPlaylist.getUuid()))
			.thenReturn(Optional.of(storedPlaylist));
		when(playlistRepository.save(storedPlaylist))
			.thenReturn(storedPlaylist);

		PlaylistResponseDTO updatedPlaylist = playlistService.update(
			storedPlaylist.getUuid(),
			playlistUpdateRequest
		);

		assertThat(updatedPlaylist.uuid()).isEqualTo(storedPlaylist.getUuid());
		assertThat(updatedPlaylist.name()).isEqualTo(playlistUpdateRequest.name());
		assertThat(updatedPlaylist.m3uFileName()).isEqualTo(playlistUpdateRequest.m3uFileName());
		assertThat(storedPlaylist.getId()).isEqualTo(originalId);
	}

	@Test
	void rejectsM3uFileNameWithDirectoryPath() {
		PlaylistRequestDTO invalidPlaylistRequest = new PlaylistRequestDTO("Playlist", "directory/playlist.m3u");

		assertThatThrownBy(() -> playlistService.store(invalidPlaylistRequest))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("M3U file name must not contain a directory path");
	}

	@Test
	void deletesPlaylistByUuid() {
		Playlist playlistToDelete = PlaylistFactory.toEntity();

		when(playlistRepository.findByUuid(playlistToDelete.getUuid()))
			.thenReturn(Optional.of(playlistToDelete));

		playlistService.delete(playlistToDelete.getUuid());

		verify(playlistRepository).delete(playlistToDelete);
	}

	@Test
	void rejectsUnknownPlaylist() {
		when(playlistRepository.findByUuid("unknown"))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> playlistService.show("unknown"))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Playlist not found with uuid: unknown");
	}
}
