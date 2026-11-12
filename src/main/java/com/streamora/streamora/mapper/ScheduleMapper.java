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

	public static Schedule toEntity(ScheduleRequestDTO request, Playlist playlist) {
		return toEntity(request, playlist, new Schedule());
	}

	public static Schedule toEntity(
		ScheduleRequestDTO request,
		Playlist playlist,
		Schedule schedule
	)
	{
		schedule.setPlaylist(playlist);
		schedule.setDayOfWeek(request.dayOfWeek());
		schedule.setStartTime(request.startTime());
		schedule.setEndTime(request.endTime());

		return schedule;
	}
}
