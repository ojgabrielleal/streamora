package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.streamora.streamora.dto.PlaylistRequestDTO;
import com.streamora.streamora.dto.PlaylistResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistFile;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.repository.PlaylistRepository;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTests {

	@Mock
	private PlaylistRepository playlistRepository;

	private PlaylistService playlistService;

	@BeforeEach
	void setUp() {
		playlistService = new PlaylistService(playlistRepository);
	}

	@Test
	void indexesPlaylists() {
		List<Playlist> playlists = List.of(playlist("playlist-uuid", "Playlist"));
		when(playlistRepository.findAll()).thenReturn(playlists);

		assertThat(playlistService.index())
				.extracting(PlaylistResponseDTO::uuid)
				.containsExactly("playlist-uuid");
	}

	@Test
	void showsPlaylistByUuid() {
		Playlist playlist = playlist("playlist-uuid", "Playlist");
		when(playlistRepository.findByUuid("playlist-uuid")).thenReturn(Optional.of(playlist));

		assertThat(playlistService.show("playlist-uuid").uuid()).isEqualTo("playlist-uuid");
	}

	@Test
	void storesPlaylist() {
		when(playlistRepository.save(org.mockito.ArgumentMatchers.any(Playlist.class)))
				.thenAnswer(invocation -> {
					Playlist playlist = invocation.getArgument(0);
					assertThat(playlist.getUuid()).isNull();
					playlist.setUuid("generated-by-persistence");
					return playlist;
				});

		PlaylistResponseDTO response = playlistService.store(new PlaylistRequestDTO("Playlist"));

		assertThat(response.uuid()).isEqualTo("generated-by-persistence");
		assertThat(response.name()).isEqualTo("Playlist");
	}

	@Test
	void updatesNameAndPreservesIdentityAndFiles() {
		Playlist storedPlaylist = playlist("stored-uuid", "Old name");
		storedPlaylist.setId(1L);
		PlaylistFile file = new PlaylistFile();
		file.setUuid("file-uuid");
		storedPlaylist.getFiles().add(file);
		when(playlistRepository.findByUuid("stored-uuid")).thenReturn(Optional.of(storedPlaylist));
		when(playlistRepository.save(storedPlaylist)).thenReturn(storedPlaylist);

		PlaylistResponseDTO updatedPlaylist = playlistService.update(
				"stored-uuid",
				new PlaylistRequestDTO("New name")
		);

		assertThat(updatedPlaylist.uuid()).isEqualTo("stored-uuid");
		assertThat(updatedPlaylist.name()).isEqualTo("New name");
		assertThat(updatedPlaylist.fileUuids()).containsExactly("file-uuid");
		assertThat(storedPlaylist.getId()).isEqualTo(1L);
	}

	@Test
	void deletesPlaylistByUuid() {
		Playlist playlist = playlist("playlist-uuid", "Playlist");
		when(playlistRepository.findByUuid("playlist-uuid")).thenReturn(Optional.of(playlist));

		playlistService.delete("playlist-uuid");

		verify(playlistRepository).delete(playlist);
	}

	@Test
	void rejectsUnknownPlaylist() {
		when(playlistRepository.findByUuid("unknown")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> playlistService.show("unknown"))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Playlist not found with uuid: unknown");
	}

	private Playlist playlist(String uuid, String name) {
		Playlist playlist = new Playlist();
		playlist.setUuid(uuid);
		playlist.setName(name);
		return playlist;
	}
}
