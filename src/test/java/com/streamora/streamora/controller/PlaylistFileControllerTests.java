package com.streamora.streamora.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.RequestMapping;

import com.streamora.streamora.dto.PlaylistFileRequestDTO;
import com.streamora.streamora.dto.PlaylistFileResponseDTO;
import com.streamora.streamora.entity.type.PlaylistFileType;
import com.streamora.streamora.service.PlaylistFileService;

@ExtendWith(MockitoExtension.class)
class PlaylistFileControllerTests {

	@Mock
	private PlaylistFileService playlistFileService;

	private PlaylistFileController playlistFileController;

	@BeforeEach
	void setUp() {
		playlistFileController = new PlaylistFileController(playlistFileService);
	}

	@Test
	void usesPlaylistFilePrefix() {
		assertThat(PlaylistFileController.class.getAnnotation(RequestMapping.class).value())
				.containsExactly("/playlist-file");
	}

	@Test
	void delegatesCrudOperationsToService() {
		PlaylistFileRequestDTO request = request();
		PlaylistFileResponseDTO response = response();
		List<PlaylistFileResponseDTO> responses = List.of(response);
		when(playlistFileService.index()).thenReturn(responses);
		when(playlistFileService.show("file-uuid")).thenReturn(response);
		when(playlistFileService.store(request)).thenReturn(response);
		when(playlistFileService.update("file-uuid", request)).thenReturn(response);

		assertThat(playlistFileController.index()).isSameAs(responses);
		assertThat(playlistFileController.show("file-uuid")).isSameAs(response);
		assertThat(playlistFileController.store(request)).isSameAs(response);
		assertThat(playlistFileController.update("file-uuid", request)).isSameAs(response);
		playlistFileController.delete("file-uuid");

		verify(playlistFileService).index();
		verify(playlistFileService).show("file-uuid");
		verify(playlistFileService).store(request);
		verify(playlistFileService).update("file-uuid", request);
		verify(playlistFileService).delete("file-uuid");
	}

	@Test
	void delegatesPlaylistAssociationsToService() {
		PlaylistFileResponseDTO response = response();
		when(playlistFileService.addToPlaylist("file-uuid", "playlist-uuid")).thenReturn(response);
		when(playlistFileService.removeFromPlaylist("file-uuid", "playlist-uuid")).thenReturn(response);

		assertThat(playlistFileController.addToPlaylist("file-uuid", "playlist-uuid"))
				.isSameAs(response);
		assertThat(playlistFileController.removeFromPlaylist("file-uuid", "playlist-uuid"))
				.isSameAs(response);

		verify(playlistFileService).addToPlaylist("file-uuid", "playlist-uuid");
		verify(playlistFileService).removeFromPlaylist("file-uuid", "playlist-uuid");
	}

	private PlaylistFileRequestDTO request() {
		return new PlaylistFileRequestDTO(PlaylistFileType.music, "File", "/audio/file.mp3");
	}

	private PlaylistFileResponseDTO response() {
		return new PlaylistFileResponseDTO(
				"file-uuid",
				PlaylistFileType.music,
				"File",
				"/audio/file.mp3",
				Set.of("playlist-uuid")
		);
	}
}
