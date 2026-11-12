package com.streamora.streamora.mapper;

import com.streamora.streamora.dto.ScheduleRequestDTO;
import com.streamora.streamora.dto.ScheduleResponseDTO;
import com.streamora.streamora.dto.PlaylistSummaryDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.Schedule;

public final class ScheduleMapper {

	private ScheduleMapper() {
	}

	public static ScheduleResponseDTO toResponse(Schedule schedule) {
		Playlist playlist = schedule.getPlaylist();

		return new ScheduleResponseDTO(
			schedule.getUuid(),
			new PlaylistSummaryDTO(playlist.getUuid(), playlist.getName()),
			schedule.getDayOfWeek(),
			schedule.getStartTime(),
			schedule.getEndTime()
		);
	}

	public static Schedule toEntity(ScheduleRequestDTO scheduleRequest, Playlist playlist) {
		return toEntity(scheduleRequest, playlist, new Schedule());
	}

	public static Schedule toEntity(
		ScheduleRequestDTO scheduleRequest,
		Playlist playlist,
		Schedule schedule
	)
	{
		schedule.setPlaylist(playlist);
		schedule.setDayOfWeek(scheduleRequest.dayOfWeek());
		schedule.setStartTime(scheduleRequest.startTime());
		schedule.setEndTime(scheduleRequest.endTime());

		return schedule;
	}
}
