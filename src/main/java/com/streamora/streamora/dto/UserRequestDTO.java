package com.streamora.streamora.dto;

public record UserRequestDTO(
	String fullname,
	String nickname,
	String username,
	String password
) {}
