package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.streamora.streamora.dto.PlaylistScheduleRequestDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistSchedule;
import com.streamora.streamora.entity.type.WeekDay;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.factory.PlaylistFactory;
import com.streamora.streamora.factory.PlaylistScheduleFactory;
import com.streamora.streamora.repository.PlaylistRepository;
import com.streamora.streamora.repository.PlaylistScheduleRepository;

@ExtendWith(MockitoExtension.class)
class PlaylistScheduleServiceTests {

	@Mock
	private PlaylistScheduleRepository playlistScheduleRepository;

	@Mock
	private PlaylistRepository playlistRepository;

	@InjectMocks
	private PlaylistScheduleService playlistScheduleService;

	@Test
	void listsSchedulesWithTheirPlaylists() {
		PlaylistSchedule schedule = PlaylistScheduleFactory.toEntity();
		when(playlistScheduleRepository.findAll()).thenReturn(List.of(schedule));

		assertThat(playlistScheduleService.index()).singleElement().satisfies(response -> {
			assertThat(response.uuid()).isEqualTo(schedule.getUuid());
			assertThat(response.playlist().uuid()).isEqualTo(schedule.getPlaylist().getUuid());
		});
	}

	@Test
	void storesScheduleResolvingPlaylistByUuid() {
		PlaylistScheduleRequestDTO request = PlaylistScheduleFactory.toRequest();
		Playlist playlist = PlaylistFactory.toEntity();
		when(playlistRepository.findByUuid(request.playlistUuid())).thenReturn(Optional.of(playlist));
		when(playlistScheduleRepository.save(any(PlaylistSchedule.class))).thenAnswer(invocation -> {
			PlaylistSchedule schedule = invocation.getArgument(0);
			schedule.setUuid("generated-uuid");
			return schedule;
		});

		assertThat(playlistScheduleService.store(request).playlist().uuid()).isEqualTo(playlist.getUuid());
	}

	@Test
	void rejectsInvalidInterval() {
		PlaylistScheduleRequestDTO request = new PlaylistScheduleRequestDTO(
			"playlist-uuid-1", WeekDay.MONDAY, LocalTime.NOON, LocalTime.of(8, 0));

		assertThatThrownBy(() -> playlistScheduleService.store(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("End time must be after start time");
	}

	@Test
	void rejectsUnknownPlaylist() {
		PlaylistScheduleRequestDTO request = PlaylistScheduleFactory.toRequest();
		when(playlistRepository.findByUuid(request.playlistUuid())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> playlistScheduleService.store(request))
			.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void deletesScheduleByUuid() {
		PlaylistSchedule schedule = PlaylistScheduleFactory.toEntity();
		when(playlistScheduleRepository.findByUuid(schedule.getUuid())).thenReturn(Optional.of(schedule));

		playlistScheduleService.delete(schedule.getUuid());

		verify(playlistScheduleRepository).delete(schedule);
	}
}
