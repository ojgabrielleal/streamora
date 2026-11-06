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

import com.streamora.streamora.dto.PlaylistFileRequestDTO;
import com.streamora.streamora.dto.PlaylistFileResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistFile;
import com.streamora.streamora.entity.type.PlaylistFileType;
import com.streamora.streamora.exception.ResourceNotFoundException;
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
		List<PlaylistFile> files = List.of(file("file-uuid", "File"));
		when(playlistFileRepository.findAll()).thenReturn(files);

		assertThat(playlistFileService.index())
				.extracting(PlaylistFileResponseDTO::uuid)
				.containsExactly("file-uuid");
	}

	@Test
	void showsFileByUuid() {
		PlaylistFile file = file("file-uuid", "File");
		when(playlistFileRepository.findByUuid("file-uuid")).thenReturn(Optional.of(file));

		assertThat(playlistFileService.show("file-uuid").uuid()).isEqualTo("file-uuid");
	}

	@Test
	void storesFile() {
		when(playlistFileRepository.save(org.mockito.ArgumentMatchers.any(PlaylistFile.class)))
				.thenAnswer(invocation -> {
					PlaylistFile file = invocation.getArgument(0);
					assertThat(file.getUuid()).isNull();
					file.setUuid("generated-by-persistence");
					return file;
				});

		PlaylistFileResponseDTO response = playlistFileService.store(request("File"));

		assertThat(response.uuid()).isEqualTo("generated-by-persistence");
		assertThat(response.name()).isEqualTo("File");
	}

	@Test
	void updatesEditableFieldsAndPreservesIdentityAndPlaylists() {
		PlaylistFile storedFile = file("stored-uuid", "Old name");
		storedFile.setId(1L);
		storedFile.getPlaylists().add(playlist("playlist-uuid"));
		PlaylistFileRequestDTO changes = new PlaylistFileRequestDTO(
				PlaylistFileType.jingle,
				"New name",
				"new/path.mp3"
		);
		when(playlistFileRepository.findByUuid("stored-uuid")).thenReturn(Optional.of(storedFile));
		when(playlistFileRepository.save(storedFile)).thenReturn(storedFile);

		PlaylistFileResponseDTO updatedFile = playlistFileService.update("stored-uuid", changes);

		assertThat(updatedFile.uuid()).isEqualTo("stored-uuid");
		assertThat(updatedFile.name()).isEqualTo("New name");
		assertThat(updatedFile.type()).isEqualTo(PlaylistFileType.jingle);
		assertThat(updatedFile.path()).isEqualTo("new/path.mp3");
		assertThat(updatedFile.playlistUuids()).containsExactly("playlist-uuid");
		assertThat(storedFile.getId()).isEqualTo(1L);
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

		PlaylistFileResponseDTO result = playlistFileService.addToPlaylist("file-uuid", "playlist-uuid");

		assertThat(result.uuid()).isEqualTo("file-uuid");
		assertThat(result.playlistUuids()).containsExactly("playlist-uuid");
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

		PlaylistFileResponseDTO result = playlistFileService.removeFromPlaylist(
				"file-uuid",
				"playlist-uuid"
		);

		assertThat(result.uuid()).isEqualTo("file-uuid");
		assertThat(result.playlistUuids()).isEmpty();
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

	private PlaylistFileRequestDTO request(String name) {
		return new PlaylistFileRequestDTO(PlaylistFileType.music, name, "path.mp3");
	}
}
