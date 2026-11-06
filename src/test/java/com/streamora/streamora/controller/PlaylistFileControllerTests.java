package com.streamora.streamora.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

	@InjectMocks
	private PlaylistFileController playlistFileController;

	@Test
	void usesPlaylistFilePrefix() {
		assertThat(PlaylistFileController.class.getAnnotation(RequestMapping.class).value())
			.containsExactly("/playlist-file");
	}

	@Test
	void delegatesIndexToService() {
		List<PlaylistFileResponseDTO> playlistFileResponses = List.of(playlistFileResponse());

		when(playlistFileService.index())
			.thenReturn(playlistFileResponses);

		assertThat(playlistFileController.index()).isSameAs(playlistFileResponses);

		verify(playlistFileService).index();
	}

	@Test
	void delegatesShowToService() {
		PlaylistFileResponseDTO playlistFileResponse = playlistFileResponse();

		when(playlistFileService.show("file-uuid"))
			.thenReturn(playlistFileResponse);

		assertThat(playlistFileController.show("file-uuid")).isSameAs(playlistFileResponse);

		verify(playlistFileService).show("file-uuid");
	}

	@Test
	void delegatesStoreToService() {
		PlaylistFileRequestDTO playlistFileRequest = playlistFileRequest();
		PlaylistFileResponseDTO playlistFileResponse = playlistFileResponse();

		when(playlistFileService.store(playlistFileRequest))
			.thenReturn(playlistFileResponse);

		assertThat(playlistFileController.store(playlistFileRequest)).isSameAs(playlistFileResponse);

		verify(playlistFileService).store(playlistFileRequest);
	}

	@Test
	void delegatesUpdateToService() {
		PlaylistFileRequestDTO playlistFileRequest = playlistFileRequest();
		PlaylistFileResponseDTO playlistFileResponse = playlistFileResponse();

		when(playlistFileService.update("file-uuid", playlistFileRequest))
			.thenReturn(playlistFileResponse);

		assertThat(playlistFileController.update("file-uuid", playlistFileRequest)).isSameAs(playlistFileResponse);

		verify(playlistFileService).update("file-uuid", playlistFileRequest);
	}

	@Test
	void delegatesDeleteToService() {
		playlistFileController.delete("file-uuid");

		verify(playlistFileService).delete("file-uuid");
	}

	@Test
	void delegatesAddToPlaylistToService() {
		PlaylistFileResponseDTO playlistFileResponse = playlistFileResponse();

		when(playlistFileService.addToPlaylist("file-uuid", "playlist-uuid"))
			.thenReturn(playlistFileResponse);

		assertThat(playlistFileController.addToPlaylist("file-uuid", "playlist-uuid"))
			.isSameAs(playlistFileResponse);

		verify(playlistFileService).addToPlaylist("file-uuid", "playlist-uuid");
	}

	@Test
	void delegatesRemoveFromPlaylistToService() {
		PlaylistFileResponseDTO playlistFileResponse = playlistFileResponse();

		when(playlistFileService.removeFromPlaylist("file-uuid", "playlist-uuid"))
			.thenReturn(playlistFileResponse);

		assertThat(playlistFileController.removeFromPlaylist("file-uuid", "playlist-uuid"))
			.isSameAs(playlistFileResponse);

		verify(playlistFileService).removeFromPlaylist("file-uuid", "playlist-uuid");
	}

	private PlaylistFileRequestDTO playlistFileRequest() {
		return new PlaylistFileRequestDTO(PlaylistFileType.music, "File", "/audio/file.mp3");
	}

	private PlaylistFileResponseDTO playlistFileResponse() {
		return new PlaylistFileResponseDTO(
			"file-uuid",
			PlaylistFileType.music,
			"File",
			"/audio/file.mp3",
			Set.of("playlist-uuid")
		);
	}
}
