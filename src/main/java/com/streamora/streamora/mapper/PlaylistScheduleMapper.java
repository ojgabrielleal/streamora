package com.streamora.streamora.mapper;

import com.streamora.streamora.dto.PlaylistScheduleRequestDTO;
import com.streamora.streamora.dto.PlaylistScheduleResponseDTO;
import com.streamora.streamora.dto.PlaylistSummaryDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistSchedule;

public final class PlaylistScheduleMapper {

	private PlaylistScheduleMapper() {
	}

	public static PlaylistScheduleResponseDTO toResponse(PlaylistSchedule schedule) {
		Playlist playlist = schedule.getPlaylist();

		return new PlaylistScheduleResponseDTO(
			schedule.getUuid(),
			new PlaylistSummaryDTO(playlist.getUuid(), playlist.getName()),
			schedule.getDayOfWeek(),
			schedule.getStartTime(),
			schedule.getEndTime()
		);
	}

	public static PlaylistSchedule toEntity(PlaylistScheduleRequestDTO request, Playlist playlist) {
		return toEntity(request, playlist, new PlaylistSchedule());
	}

	public static PlaylistSchedule toEntity(
		PlaylistScheduleRequestDTO request,
		Playlist playlist,
		PlaylistSchedule schedule
	)
	{
		schedule.setPlaylist(playlist);
		schedule.setDayOfWeek(request.dayOfWeek());
		schedule.setStartTime(request.startTime());
		schedule.setEndTime(request.endTime());

		return schedule;
	}
}
