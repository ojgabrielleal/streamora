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

import com.streamora.streamora.entity.Playlist;
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
		List<Playlist> playlists = List.of(new Playlist());
		when(playlistRepository.findAll()).thenReturn(playlists);

		assertThat(playlistService.index()).isSameAs(playlists);
	}

	@Test
	void showsPlaylistByUuid() {
		Playlist playlist = playlist("playlist-uuid", "Playlist");
		when(playlistRepository.findByUuid("playlist-uuid")).thenReturn(Optional.of(playlist));

		assertThat(playlistService.show("playlist-uuid")).isSameAs(playlist);
	}

	@Test
	void storesPlaylist() {
		Playlist playlist = playlist("playlist-uuid", "Playlist");
		when(playlistRepository.save(playlist)).thenReturn(playlist);

		assertThat(playlistService.store(playlist)).isSameAs(playlist);
	}

	@Test
	void updatesNameAndPreservesIdentityAndFiles() {
		Playlist storedPlaylist = playlist("stored-uuid", "Old name");
		storedPlaylist.setId(1L);
		storedPlaylist.getFiles().add(new com.streamora.streamora.entity.PlaylistFile());
		Playlist changes = playlist("other-uuid", "New name");
		when(playlistRepository.findByUuid("stored-uuid")).thenReturn(Optional.of(storedPlaylist));
		when(playlistRepository.save(storedPlaylist)).thenReturn(storedPlaylist);

		Playlist updatedPlaylist = playlistService.update("stored-uuid", changes);

		assertThat(updatedPlaylist.getId()).isEqualTo(1L);
		assertThat(updatedPlaylist.getUuid()).isEqualTo("stored-uuid");
		assertThat(updatedPlaylist.getName()).isEqualTo("New name");
		assertThat(updatedPlaylist.getFiles()).hasSize(1);
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
