package com.streamora.streamora.mapper;

import java.util.stream.Collectors;

import com.streamora.streamora.dto.PlaylistFileRequestDTO;
import com.streamora.streamora.dto.PlaylistFileResponseDTO;
import com.streamora.streamora.entity.PlaylistFile;

public final class PlaylistFileMapper {

	private PlaylistFileMapper() {
	}

	public static PlaylistFileResponseDTO toResponse(PlaylistFile playlistFile) {
		return new PlaylistFileResponseDTO(
			playlistFile.getUuid(),
			playlistFile.getType(),
			playlistFile.getName(),
			playlistFile.getPath(),
			playlistFile.getPlaylists().stream()
				.map(playlist -> playlist.getUuid())
				.collect(Collectors.toUnmodifiableSet())
		);
	}

	public static PlaylistFile toEntity(PlaylistFileRequestDTO request) {
		return toEntity(request, new PlaylistFile());
	}

	public static PlaylistFile toEntity(PlaylistFileRequestDTO request, PlaylistFile playlistFile) {
		playlistFile.setType(request.type());
		playlistFile.setName(request.name());
		playlistFile.setPath(request.path());

		return playlistFile;
	}
}
