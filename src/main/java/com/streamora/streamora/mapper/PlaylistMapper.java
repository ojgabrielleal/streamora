package com.streamora.streamora.mapper;

import java.util.stream.Collectors;

import com.streamora.streamora.dto.playlists.PlaylistRequestDTO;
import com.streamora.streamora.dto.playlists.PlaylistResponseDTO;
import com.streamora.streamora.dto.schedules.ScheduleSummaryDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.Schedule;

public final class PlaylistMapper {

	private PlaylistMapper() {
	}

	public static PlaylistResponseDTO toResponse(Playlist playlist) {
		return new PlaylistResponseDTO(
			playlist.getUuid(),
			playlist.getName(),
			playlist.getM3uFileName(),
			playlist.getSchedules().stream()
				.map(PlaylistMapper::toScheduleSummary)
				.collect(Collectors.toUnmodifiableSet())
		);
	}

	public static Playlist toEntity(PlaylistRequestDTO playlistRequest) {
		return toEntity(playlistRequest, new Playlist());
	}

	public static Playlist toEntity(PlaylistRequestDTO playlistRequest, Playlist playlist) {
		validateM3uFileName(playlistRequest.m3uFileName());
		
		playlist.setName(playlistRequest.name());
		playlist.setM3uFileName(playlistRequest.m3uFileName());

		return playlist;
	}

	private static void validateM3uFileName(String m3uFileName) {
		if (m3uFileName != null && (m3uFileName.contains("/") || m3uFileName.contains("\\"))) {
			throw new IllegalArgumentException("M3U file name must not contain a directory path");
		}
	}

	private static ScheduleSummaryDTO toScheduleSummary(Schedule schedule) {
		return new ScheduleSummaryDTO(
			schedule.getUuid(),
			schedule.getDayOfWeek(),
			schedule.getStartTime(),
			schedule.getEndTime()
		);
	}
}
