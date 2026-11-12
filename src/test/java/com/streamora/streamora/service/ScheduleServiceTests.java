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

import com.streamora.streamora.dto.ScheduleRequestDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.Schedule;
import com.streamora.streamora.entity.type.WeekDay;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.factory.PlaylistFactory;
import com.streamora.streamora.factory.ScheduleFactory;
import com.streamora.streamora.repository.PlaylistRepository;
import com.streamora.streamora.repository.ScheduleRepository;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTests {

	@Mock
	private ScheduleRepository scheduleRepository;

	@Mock
	private PlaylistRepository playlistRepository;

	@InjectMocks
	private ScheduleService scheduleService;

	@Test
	void listsSchedulesWithTheirPlaylists() {
		Schedule schedule = ScheduleFactory.toEntity();

		when(scheduleRepository.findAll())
			.thenReturn(List.of(schedule));

		assertThat(scheduleService.index()).singleElement().satisfies(response -> {
			assertThat(response.uuid()).isEqualTo(schedule.getUuid());
			assertThat(response.playlist().uuid()).isEqualTo(schedule.getPlaylist().getUuid());
		});
	}

	@Test
	void storesScheduleResolvingPlaylistByUuid() {
		ScheduleRequestDTO request = ScheduleFactory.toRequest();
		Playlist playlist = PlaylistFactory.toEntity();

		when(playlistRepository.findByUuid(request.playlistUuid()))
			.thenReturn(Optional.of(playlist));
		when(scheduleRepository.save(any(Schedule.class)))
			.thenAnswer(invocation -> {
				Schedule schedule = invocation.getArgument(0);
				schedule.setUuid("generated-uuid");
				return schedule;
			});

		assertThat(scheduleService.store(request).playlist().uuid()).isEqualTo(playlist.getUuid());
	}

	@Test
	void rejectsInvalidInterval() {
		ScheduleRequestDTO request = new ScheduleRequestDTO(
			"playlist-uuid-1", WeekDay.MONDAY, LocalTime.NOON, LocalTime.of(8, 0));

		assertThatThrownBy(() -> scheduleService.store(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("End time must be after start time");
	}

	@Test
	void rejectsUnknownPlaylist() {
		ScheduleRequestDTO request = ScheduleFactory.toRequest();

		when(playlistRepository.findByUuid(request.playlistUuid()))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> scheduleService.store(request))
			.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void deletesScheduleByUuid() {
		Schedule schedule = ScheduleFactory.toEntity();

		when(scheduleRepository.findByUuid(schedule.getUuid()))
			.thenReturn(Optional.of(schedule));

		scheduleService.delete(schedule.getUuid());

		verify(scheduleRepository).delete(schedule);
	}
}
