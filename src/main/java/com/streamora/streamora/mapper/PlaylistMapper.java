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
			playlist.isBase(),
			playlist.getWeight(),
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
		validateWeight(playlistRequest.weight());

		playlist.setName(playlistRequest.name());
		playlist.setM3uFileName(playlistRequest.m3uFileName());
		playlist.setBase(playlistRequest.isBase());
		playlist.setWeight(playlistRequest.weight());

		return playlist;
	}

	private static void validateWeight(Integer weight) {
		if (weight == null || weight < 1) {
			throw new IllegalArgumentException("Playlist weight must be greater than zero");
		}
	}

	public static void validateM3uFileName(String m3uFileName) {
		if (m3uFileName != null && !m3uFileName.matches("[A-Za-z0-9._-]+\\.m3u")) {
			throw new IllegalArgumentException("M3U file name must be a simple .m3u file name");
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
