package com.streamora.streamora.dto;

import java.util.Set;

import com.streamora.streamora.entity.type.PlaylistFileType;

public record PlaylistFileResponseDTO(
	String uuid,
	PlaylistFileType type,
	String name,
	String path,
	Set<String> playlistUuids
) {}
