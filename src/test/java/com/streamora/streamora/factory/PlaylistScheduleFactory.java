package com.streamora.streamora.factory;

import java.time.LocalTime;

import com.streamora.streamora.dto.PlaylistScheduleRequestDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistSchedule;
import com.streamora.streamora.entity.type.WeekDay;

public final class PlaylistScheduleFactory {

	private PlaylistScheduleFactory() {
	}

	public static PlaylistScheduleRequestDTO toRequest() {
		return new PlaylistScheduleRequestDTO(
			"playlist-uuid-1",
			WeekDay.MONDAY,
			LocalTime.of(8, 0),
			LocalTime.of(12, 0)
		);
	}

	public static PlaylistSchedule toEntity() {
		Playlist playlist = PlaylistFactory.toEntity();
		PlaylistSchedule schedule = new PlaylistSchedule();
		schedule.setId(1L);
		schedule.setUuid("schedule-uuid-1");
		schedule.setPlaylist(playlist);
		schedule.setDayOfWeek(WeekDay.MONDAY);
		schedule.setStartTime(LocalTime.of(8, 0));
		schedule.setEndTime(LocalTime.of(12, 0));
		return schedule;
	}
}
