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

import com.streamora.streamora.dto.PlaylistRequestDTO;
import com.streamora.streamora.dto.PlaylistResponseDTO;
import com.streamora.streamora.service.PlaylistService;

@ExtendWith(MockitoExtension.class)
class PlaylistControllerTests {

	@Mock
	private PlaylistService playlistService;

	private PlaylistController playlistController;

	@BeforeEach
	void setUp() {
		playlistController = new PlaylistController(playlistService);
	}

	@Test
	void usesPlaylistPrefix() {
		assertThat(PlaylistController.class.getAnnotation(RequestMapping.class).value())
				.containsExactly("/playlist");
	}

	@Test
	void delegatesCrudOperationsToService() {
		PlaylistRequestDTO request = new PlaylistRequestDTO("Playlist");
		PlaylistResponseDTO response = new PlaylistResponseDTO("playlist-uuid", "Playlist", Set.of());
		List<PlaylistResponseDTO> responses = List.of(response);
		when(playlistService.index()).thenReturn(responses);
		when(playlistService.show("playlist-uuid")).thenReturn(response);
		when(playlistService.store(request)).thenReturn(response);
		when(playlistService.update("playlist-uuid", request)).thenReturn(response);

		assertThat(playlistController.index()).isSameAs(responses);
		assertThat(playlistController.show("playlist-uuid")).isSameAs(response);
		assertThat(playlistController.store(request)).isSameAs(response);
		assertThat(playlistController.update("playlist-uuid", request)).isSameAs(response);
		playlistController.delete("playlist-uuid");

		verify(playlistService).index();
		verify(playlistService).show("playlist-uuid");
		verify(playlistService).store(request);
		verify(playlistService).update("playlist-uuid", request);
		verify(playlistService).delete("playlist-uuid");
	}
}
