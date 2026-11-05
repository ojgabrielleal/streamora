package com.streamora.streamora.dto;

public record UserResponseDTO(
	String uuid,
	String fullname,
	String nickname,
	String username
) {}
