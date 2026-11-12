package com.streamora.streamora.factory;

import java.time.LocalTime;

import com.streamora.streamora.dto.ScheduleRequestDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.Schedule;
import com.streamora.streamora.entity.type.WeekDay;

public final class ScheduleFactory {

	private ScheduleFactory() {
	}

	public static ScheduleRequestDTO toRequest() {
		return new ScheduleRequestDTO(
			"playlist-uuid-1",
			WeekDay.MONDAY,
			LocalTime.of(8, 0),
			LocalTime.of(12, 0)
		);
	}

	public static Schedule toEntity() {
		Playlist playlist = PlaylistFactory.toEntity();
		Schedule schedule = new Schedule();

		schedule.setId(1L);
		schedule.setUuid("schedule-uuid-1");
		schedule.setPlaylist(playlist);
		schedule.setDayOfWeek(WeekDay.MONDAY);
		schedule.setStartTime(LocalTime.of(8, 0));
		schedule.setEndTime(LocalTime.of(12, 0));

		return schedule;
	}
}
