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

import com.streamora.streamora.dto.PlaylistRequestDTO;
import com.streamora.streamora.dto.PlaylistResponseDTO;
import com.streamora.streamora.service.PlaylistService;

@ExtendWith(MockitoExtension.class)
class PlaylistControllerTests {

	@Mock
	private PlaylistService playlistService;

	@InjectMocks
	private PlaylistController playlistController;

	@Test
	void usesPlaylistPrefix() {
		assertThat(PlaylistController.class.getAnnotation(RequestMapping.class).value())
			.containsExactly("/playlist");
	}

	@Test
	void delegatesIndexToService() {
		List<PlaylistResponseDTO> playlistResponses = List.of(playlistResponse());

		when(playlistService.index())
			.thenReturn(playlistResponses);

		assertThat(playlistController.index()).isSameAs(playlistResponses);

		verify(playlistService).index();
	}

	@Test
	void delegatesShowToService() {
		PlaylistResponseDTO playlistResponse = playlistResponse();

		when(playlistService.show("playlist-uuid"))
			.thenReturn(playlistResponse);

		assertThat(playlistController.show("playlist-uuid")).isSameAs(playlistResponse);

		verify(playlistService).show("playlist-uuid");
	}

	@Test
	void delegatesStoreToService() {
		PlaylistRequestDTO playlistRequest = playlistRequest();
		PlaylistResponseDTO playlistResponse = playlistResponse();

		when(playlistService.store(playlistRequest))
			.thenReturn(playlistResponse);

		assertThat(playlistController.store(playlistRequest)).isSameAs(playlistResponse);

		verify(playlistService).store(playlistRequest);
	}

	@Test
	void delegatesUpdateToService() {
		PlaylistRequestDTO playlistRequest = playlistRequest();
		PlaylistResponseDTO playlistResponse = playlistResponse();

		when(playlistService.update("playlist-uuid", playlistRequest))
			.thenReturn(playlistResponse);

		assertThat(playlistController.update("playlist-uuid", playlistRequest)).isSameAs(playlistResponse);

		verify(playlistService).update("playlist-uuid", playlistRequest);
	}

	@Test
	void delegatesDeleteToService() {
		playlistController.delete("playlist-uuid");

		verify(playlistService).delete("playlist-uuid");
	}

	private PlaylistRequestDTO playlistRequest() {
		return new PlaylistRequestDTO("Playlist");
	}

	private PlaylistResponseDTO playlistResponse() {
		return new PlaylistResponseDTO("playlist-uuid", "Playlist", Set.of(), Set.of());
	}
}
