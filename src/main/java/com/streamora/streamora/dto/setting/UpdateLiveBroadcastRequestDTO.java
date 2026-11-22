package com.streamora.streamora.dto.setting;

import jakarta.validation.constraints.NotNull;

public record UpdateLiveBroadcastRequestDTO(
	@NotNull
	Boolean enabled
) {}
