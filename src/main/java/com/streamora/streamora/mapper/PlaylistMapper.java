package com.streamora.streamora.mapper;

import java.util.stream.Collectors;

import com.streamora.streamora.dto.PlaylistRequestDTO;
import com.streamora.streamora.dto.PlaylistResponseDTO;
import com.streamora.streamora.entity.Playlist;

public final class PlaylistMapper {

	private PlaylistMapper() {
	}

	public static PlaylistResponseDTO toResponse(Playlist playlist) {
		return new PlaylistResponseDTO(
			playlist.getUuid(),
			playlist.getName(),
			playlist.getFiles().stream()
				.map(playlistFile -> playlistFile.getUuid())
				.collect(Collectors.toUnmodifiableSet())
		);
	}

	public static Playlist toEntity(PlaylistRequestDTO playlistRequest) {
		return toEntity(playlistRequest, new Playlist());
	}

	public static Playlist toEntity(PlaylistRequestDTO playlistRequest, Playlist playlist) {
		playlist.setName(playlistRequest.name());

		return playlist;
	}
}
