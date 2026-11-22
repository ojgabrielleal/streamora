package com.streamora.streamora.dto.liquidsoap;

import jakarta.validation.constraints.NotBlank;

public record LiquidsoapAuthRequestDTO(
	@NotBlank
	String username,
	@NotBlank
	String password
) {}
