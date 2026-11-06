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

import com.streamora.streamora.dto.PlaylistFileRequestDTO;
import com.streamora.streamora.dto.PlaylistFileResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistFile;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.factory.PlaylistFactory;
import com.streamora.streamora.factory.PlaylistFileFactory;
import com.streamora.streamora.repository.PlaylistFileRepository;
import com.streamora.streamora.repository.PlaylistRepository;

@ExtendWith(MockitoExtension.class)
class PlaylistFileServiceTests {

	@Mock
	private PlaylistFileRepository playlistFileRepository;

	@Mock
	private PlaylistRepository playlistRepository;

	@InjectMocks
	private PlaylistFileService playlistFileService;

	@Test
	void indexesFiles() {
		List<PlaylistFile> playlistFileEntities = PlaylistFileFactory.toEntity(2);

		when(playlistFileRepository.findAll())
			.thenReturn(playlistFileEntities);

		assertThat(playlistFileService.index())
			.extracting(playlistFile -> playlistFile.uuid())
			.containsExactly(playlistFileEntities.getFirst().getUuid(), playlistFileEntities.getLast().getUuid());
	}

	@Test
	void showsFileByUuid() {
		PlaylistFile playlistFileEntity = PlaylistFileFactory.toEntity();

		when(playlistFileRepository.findByUuid(playlistFileEntity.getUuid()))
			.thenReturn(Optional.of(playlistFileEntity));

		assertThat(playlistFileService.show(playlistFileEntity.getUuid()).uuid()).isEqualTo(playlistFileEntity.getUuid());
	}

	@Test
	void storesFile() {
		PlaylistFileRequestDTO playlistFileCreateRequest = PlaylistFileFactory.toCreate();

		when(playlistFileRepository.save(org.mockito.ArgumentMatchers.any(PlaylistFile.class)))
			.thenAnswer(invocation -> {
				PlaylistFile playlistFileToPersist = invocation.getArgument(0);
				assertThat(playlistFileToPersist.getUuid()).isNull();
				playlistFileToPersist.setUuid("generated-by-persistence");
				return playlistFileToPersist;
			});

		PlaylistFileResponseDTO createdPlaylistFile = playlistFileService.store(playlistFileCreateRequest);

		assertThat(createdPlaylistFile.uuid()).isEqualTo("generated-by-persistence");
		assertThat(createdPlaylistFile.name()).isEqualTo(playlistFileCreateRequest.name());
	}

	@Test
	void updatesEditableFieldsAndPreservesIdentityAndPlaylists() {
		PlaylistFile storedFile = PlaylistFileFactory.toEntity();
		Playlist playlist = PlaylistFactory.toEntity();
		storedFile.getPlaylists().add(playlist);
		PlaylistFileRequestDTO playlistFileUpdateRequest = PlaylistFileFactory.toCreate(2).getLast();
		Long originalId = storedFile.getId();

		when(playlistFileRepository.findByUuid(storedFile.getUuid()))
			.thenReturn(Optional.of(storedFile));

		when(playlistFileRepository.save(storedFile))
			.thenReturn(storedFile);

		PlaylistFileResponseDTO updatedFile = playlistFileService.update(storedFile.getUuid(), playlistFileUpdateRequest);

		assertThat(updatedFile.uuid()).isEqualTo(storedFile.getUuid());
		assertThat(updatedFile.name()).isEqualTo(playlistFileUpdateRequest.name());
		assertThat(updatedFile.type()).isEqualTo(playlistFileUpdateRequest.type());
		assertThat(updatedFile.path()).isEqualTo(playlistFileUpdateRequest.path());
		assertThat(updatedFile.playlistUuids()).containsExactly(playlist.getUuid());
		assertThat(storedFile.getId()).isEqualTo(originalId);
	}

	@Test
	void deletesFileByUuid() {
		PlaylistFile file = PlaylistFileFactory.toEntity();

		when(playlistFileRepository.findByUuid(file.getUuid()))
			.thenReturn(Optional.of(file));

		playlistFileService.delete(file.getUuid());

		verify(playlistFileRepository).delete(file);
	}

	@Test
	void addsFileToPlaylistOnBothSides() {
		PlaylistFile playlistFile = PlaylistFileFactory.toEntity();
		Playlist playlist = PlaylistFactory.toEntity();

		when(playlistFileRepository.findByUuid(playlistFile.getUuid()))
			.thenReturn(Optional.of(playlistFile));

		when(playlistRepository.findByUuid(playlist.getUuid()))
			.thenReturn(Optional.of(playlist));

		PlaylistFileResponseDTO associatedPlaylistFile = playlistFileService.addToPlaylist(
			playlistFile.getUuid(), playlist.getUuid()
		);

		assertThat(associatedPlaylistFile.uuid()).isEqualTo(playlistFile.getUuid());
		assertThat(associatedPlaylistFile.playlistUuids()).containsExactly(playlist.getUuid());
		assertThat(playlist.getFiles()).containsExactly(playlistFile);
		assertThat(playlistFile.getPlaylists()).containsExactly(playlist);
		verify(playlistRepository).save(playlist);
	}

	@Test
	void removesFileFromPlaylistOnBothSides() {
		PlaylistFile playlistFile = PlaylistFileFactory.toEntity();
		Playlist playlist = PlaylistFactory.toEntity();
		playlist.getFiles().add(playlistFile);
		playlistFile.getPlaylists().add(playlist);

		when(playlistFileRepository.findByUuid(playlistFile.getUuid()))
			.thenReturn(Optional.of(playlistFile));

		when(playlistRepository.findByUuid(playlist.getUuid()))
			.thenReturn(Optional.of(playlist));

		PlaylistFileResponseDTO dissociatedPlaylistFile = playlistFileService.removeFromPlaylist(
			playlistFile.getUuid(),
			playlist.getUuid()
		);

		assertThat(dissociatedPlaylistFile.uuid()).isEqualTo(playlistFile.getUuid());
		assertThat(dissociatedPlaylistFile.playlistUuids()).isEmpty();
		assertThat(playlist.getFiles()).isEmpty();
		assertThat(playlistFile.getPlaylists()).isEmpty();
		verify(playlistRepository).save(playlist);
	}

	@Test
	void rejectsUnknownFile() {
		when(playlistFileRepository.findByUuid("unknown"))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> playlistFileService.show("unknown"))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("PlaylistFile not found with uuid: unknown");
	}

	@Test
	void rejectsUnknownPlaylistWhenAddingFile() {
		PlaylistFile file = PlaylistFileFactory.toEntity();

		when(playlistFileRepository.findByUuid(file.getUuid()))
			.thenReturn(Optional.of(file));

		when(playlistRepository.findByUuid("unknown"))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> playlistFileService.addToPlaylist(file.getUuid(), "unknown"))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Playlist not found with uuid: unknown");
	}
}
