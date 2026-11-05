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
import com.streamora.streamora.entity.PlaylistFile;
import com.streamora.streamora.entity.type.PlaylistFileType;
import com.streamora.streamora.repository.PlaylistFileRepository;
import com.streamora.streamora.repository.PlaylistRepository;

@ExtendWith(MockitoExtension.class)
class PlaylistFileServiceTests {

	@Mock
	private PlaylistFileRepository playlistFileRepository;

	@Mock
	private PlaylistRepository playlistRepository;

	private PlaylistFileService playlistFileService;

	@BeforeEach
	void setUp() {
		playlistFileService = new PlaylistFileService(playlistFileRepository, playlistRepository);
	}

	@Test
	void indexesFiles() {
		List<PlaylistFile> files = List.of(new PlaylistFile());
		when(playlistFileRepository.findAll()).thenReturn(files);

		assertThat(playlistFileService.index()).isSameAs(files);
	}

	@Test
	void showsFileByUuid() {
		PlaylistFile file = file("file-uuid", "File");
		when(playlistFileRepository.findByUuid("file-uuid")).thenReturn(Optional.of(file));

		assertThat(playlistFileService.show("file-uuid")).isSameAs(file);
	}

	@Test
	void storesFile() {
		PlaylistFile file = file("file-uuid", "File");
		when(playlistFileRepository.save(file)).thenReturn(file);

		assertThat(playlistFileService.store(file)).isSameAs(file);
	}

	@Test
	void updatesEditableFieldsAndPreservesIdentityAndPlaylists() {
		PlaylistFile storedFile = file("stored-uuid", "Old name");
		storedFile.setId(1L);
		storedFile.getPlaylists().add(new Playlist());
		PlaylistFile changes = file("other-uuid", "New name");
		changes.setType(PlaylistFileType.jingle);
		changes.setPath("new/path.mp3");
		when(playlistFileRepository.findByUuid("stored-uuid")).thenReturn(Optional.of(storedFile));
		when(playlistFileRepository.save(storedFile)).thenReturn(storedFile);

		PlaylistFile updatedFile = playlistFileService.update("stored-uuid", changes);

		assertThat(updatedFile.getId()).isEqualTo(1L);
		assertThat(updatedFile.getUuid()).isEqualTo("stored-uuid");
		assertThat(updatedFile.getName()).isEqualTo("New name");
		assertThat(updatedFile.getType()).isEqualTo(PlaylistFileType.jingle);
		assertThat(updatedFile.getPath()).isEqualTo("new/path.mp3");
		assertThat(updatedFile.getPlaylists()).hasSize(1);
	}

	@Test
	void deletesFileByUuid() {
		PlaylistFile file = file("file-uuid", "File");
		when(playlistFileRepository.findByUuid("file-uuid")).thenReturn(Optional.of(file));

		playlistFileService.delete("file-uuid");

		verify(playlistFileRepository).delete(file);
	}

	@Test
	void addsFileToPlaylistOnBothSides() {
		PlaylistFile file = file("file-uuid", "File");
		Playlist playlist = playlist("playlist-uuid");
		when(playlistFileRepository.findByUuid("file-uuid")).thenReturn(Optional.of(file));
		when(playlistRepository.findByUuid("playlist-uuid")).thenReturn(Optional.of(playlist));

		PlaylistFile result = playlistFileService.addToPlaylist("file-uuid", "playlist-uuid");

		assertThat(result).isSameAs(file);
		assertThat(playlist.getFiles()).containsExactly(file);
		assertThat(file.getPlaylists()).containsExactly(playlist);
		verify(playlistRepository).save(playlist);
	}

	@Test
	void removesFileFromPlaylistOnBothSides() {
		PlaylistFile file = file("file-uuid", "File");
		Playlist playlist = playlist("playlist-uuid");
		playlist.getFiles().add(file);
		file.getPlaylists().add(playlist);
		when(playlistFileRepository.findByUuid("file-uuid")).thenReturn(Optional.of(file));
		when(playlistRepository.findByUuid("playlist-uuid")).thenReturn(Optional.of(playlist));

		PlaylistFile result = playlistFileService.removeFromPlaylist("file-uuid", "playlist-uuid");

		assertThat(result).isSameAs(file);
		assertThat(playlist.getFiles()).isEmpty();
		assertThat(file.getPlaylists()).isEmpty();
		verify(playlistRepository).save(playlist);
	}

	@Test
	void rejectsUnknownFile() {
		when(playlistFileRepository.findByUuid("unknown")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> playlistFileService.show("unknown"))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("PlaylistFile not found with uuid: unknown");
	}

	@Test
	void rejectsUnknownPlaylistWhenAddingFile() {
		PlaylistFile file = file("file-uuid", "File");
		when(playlistFileRepository.findByUuid("file-uuid")).thenReturn(Optional.of(file));
		when(playlistRepository.findByUuid("unknown")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> playlistFileService.addToPlaylist("file-uuid", "unknown"))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Playlist not found with uuid: unknown");
	}

	private PlaylistFile file(String uuid, String name) {
		PlaylistFile file = new PlaylistFile();
		file.setUuid(uuid);
		file.setType(PlaylistFileType.music);
		file.setName(name);
		file.setPath("path.mp3");
		return file;
	}

	private Playlist playlist(String uuid) {
		Playlist playlist = new Playlist();
		playlist.setUuid(uuid);
		playlist.setName("Playlist");
		return playlist;
	}
}
