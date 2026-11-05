package com.streamora.streamora.dto;

import java.util.Set;

public record PlaylistResponseDTO(
	String uuid,
	String name,
	Set<String> fileUuids
) {}
