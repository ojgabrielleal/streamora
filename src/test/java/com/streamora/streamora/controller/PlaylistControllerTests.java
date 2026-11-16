package com.streamora.streamora.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import com.streamora.streamora.dto.playlists.PlaylistDetailsResponseDTO;
import com.streamora.streamora.dto.playlists.PlaylistErrorDTO;
import com.streamora.streamora.dto.playlists.PlaylistRequestDTO;
import com.streamora.streamora.dto.playlists.PlaylistResponseDTO;
import com.streamora.streamora.dto.playlists.PlaylistTracksRequestDTO;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapFileException;
import com.streamora.streamora.infrastructure.liquidsoap.exception.M3uReadException;
import com.streamora.streamora.service.LiquidsoapService;
import com.streamora.streamora.service.PlaylistService;

@ExtendWith(MockitoExtension.class)
class PlaylistControllerTests {

	@Mock
	private PlaylistService playlistService;

	@Mock
	private LiquidsoapService liquidsoapService;

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

		List<PlaylistResponseDTO> returnedPlaylists = playlistController.index();

		assertThat(returnedPlaylists)
			.isSameAs(playlistResponses);

		verify(playlistService).index();
	}

	@Test
	void delegatesShowToService() {
		PlaylistDetailsResponseDTO playlistDetails = playlistDetails();

		when(liquidsoapService.showPlaylistDetails("playlist-uuid"))
			.thenReturn(playlistDetails);

		PlaylistDetailsResponseDTO returnedPlaylist = playlistController.show("playlist-uuid");

		assertThat(returnedPlaylist)
			.isSameAs(playlistDetails);

		verify(liquidsoapService).showPlaylistDetails("playlist-uuid");
	}

	@Test
	void delegatesTrackReplacementToLiquidsoapService() {
		PlaylistTracksRequestDTO playlistTracksRequest = new PlaylistTracksRequestDTO(
			List.of("rock/first.mp3")
		);
		PlaylistDetailsResponseDTO playlistDetails = playlistDetails();
		when(liquidsoapService.setPlaylistTracks(
			"playlist-uuid",
			playlistTracksRequest.paths()
		))
			.thenReturn(playlistDetails);

		PlaylistDetailsResponseDTO updatedPlaylist = playlistController.setTracks(
			"playlist-uuid",
			playlistTracksRequest
		);

		assertThat(updatedPlaylist)
			.isSameAs(playlistDetails);
		verify(liquidsoapService).setPlaylistTracks(
			"playlist-uuid",
			playlistTracksRequest.paths()
		);
	}

	@Test
	void delegatesStoreToService() {
		PlaylistRequestDTO playlistRequest = playlistRequest();
		PlaylistResponseDTO playlistResponse = playlistResponse();

		when(playlistService.store(playlistRequest))
			.thenReturn(playlistResponse);

		PlaylistResponseDTO createdPlaylist = playlistController.store(playlistRequest);

		assertThat(createdPlaylist)
			.isSameAs(playlistResponse);

		verify(playlistService).store(playlistRequest);
	}

	@Test
	void delegatesUpdateToService() {
		PlaylistRequestDTO playlistRequest = playlistRequest();
		PlaylistResponseDTO playlistResponse = playlistResponse();

		when(playlistService.update("playlist-uuid", playlistRequest))
			.thenReturn(playlistResponse);

		PlaylistResponseDTO updatedPlaylist = playlistController.update("playlist-uuid", playlistRequest);

		assertThat(updatedPlaylist)
			.isSameAs(playlistResponse);

		verify(playlistService).update("playlist-uuid", playlistRequest);
	}

	@Test
	void delegatesDeleteToService() {
		playlistController.delete("playlist-uuid");

		verify(playlistService).delete("playlist-uuid");
	}

	@Test
	void translatesInvalidTrackToBadRequest() {
		ResponseEntity<PlaylistErrorDTO> response = playlistController.handleInvalidTracks(
			new LiquidsoapFileException("At least one media path is required")
		);

		assertThat(response.getStatusCode().value()).isEqualTo(400);
		assertThat(response.getBody())
			.isEqualTo(new PlaylistErrorDTO("At least one media path is required"));
	}

	@Test
	void translatesMissingPlaylistToNotFound() {
		ResponseEntity<PlaylistErrorDTO> response = playlistController.handleNotFound(
			new ResourceNotFoundException("Playlist", "playlist-uuid")
		);

		assertThat(response.getStatusCode().value()).isEqualTo(404);
		assertThat(response.getBody())
			.isEqualTo(new PlaylistErrorDTO("Playlist not found with uuid: playlist-uuid"));
	}

	@Test
	void translatesInvalidAssociatedM3uToConflict() {
		ResponseEntity<PlaylistErrorDTO> response = playlistController.handleM3uReadFailure(
			new M3uReadException("Associated M3U file is unavailable")
		);

		assertThat(response.getStatusCode().value()).isEqualTo(409);
		assertThat(response.getBody())
			.isEqualTo(new PlaylistErrorDTO("Associated M3U file is unavailable"));
	}

	private PlaylistRequestDTO playlistRequest() {
		return new PlaylistRequestDTO("Playlist", "playlist.m3u", true, 2);
	}

	private PlaylistResponseDTO playlistResponse() {
		return new PlaylistResponseDTO(
			"playlist-uuid",
			"Playlist",
			"playlist.m3u",
			true,
			2,
			java.util.Set.of()
		);
	}

	private PlaylistDetailsResponseDTO playlistDetails() {
		return new PlaylistDetailsResponseDTO(
			"playlist-uuid",
			"Playlist",
			"playlist-uuid.m3u",
			true,
			2,
			java.util.Set.of(),
			List.of("rock/first.mp3")
		);
	}
}
