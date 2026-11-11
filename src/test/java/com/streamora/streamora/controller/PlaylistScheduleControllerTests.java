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
import org.springframework.web.bind.annotation.RequestMapping;

import com.streamora.streamora.dto.PlaylistScheduleRequestDTO;
import com.streamora.streamora.dto.PlaylistScheduleResponseDTO;
import com.streamora.streamora.factory.PlaylistScheduleFactory;
import com.streamora.streamora.mapper.PlaylistScheduleMapper;
import com.streamora.streamora.service.PlaylistScheduleService;

@ExtendWith(MockitoExtension.class)
class PlaylistScheduleControllerTests {

	@Mock
	private PlaylistScheduleService playlistScheduleService;

	@InjectMocks
	private PlaylistScheduleController playlistScheduleController;

	@Test
	void usesPlaylistSchedulePrefix() {
		assertThat(PlaylistScheduleController.class.getAnnotation(RequestMapping.class).value())
			.containsExactly("/playlist-schedule");
	}

	@Test
	void delegatesCrudOperationsToService() {
		PlaylistScheduleRequestDTO request = PlaylistScheduleFactory.toRequest();
		PlaylistScheduleResponseDTO response = PlaylistScheduleMapper.toResponse(PlaylistScheduleFactory.toEntity());
		when(playlistScheduleService.index()).thenReturn(List.of(response));
		when(playlistScheduleService.show("schedule-uuid-1")).thenReturn(response);
		when(playlistScheduleService.store(request)).thenReturn(response);
		when(playlistScheduleService.update("schedule-uuid-1", request)).thenReturn(response);

		assertThat(playlistScheduleController.index()).containsExactly(response);
		assertThat(playlistScheduleController.show("schedule-uuid-1")).isSameAs(response);
		assertThat(playlistScheduleController.store(request)).isSameAs(response);
		assertThat(playlistScheduleController.update("schedule-uuid-1", request)).isSameAs(response);
		playlistScheduleController.delete("schedule-uuid-1");

		verify(playlistScheduleService).delete("schedule-uuid-1");
	}
}
