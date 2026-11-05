package com.streamora.streamora.dto;

import com.streamora.streamora.entity.type.PlaylistFileType;

public record PlaylistFileRequestDTO(
	PlaylistFileType type,
	String name,
	String path
) {}
