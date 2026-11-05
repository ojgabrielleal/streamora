package com.streamora.streamora.mapper;

import com.streamora.streamora.dto.UserRequestDTO;
import com.streamora.streamora.dto.UserResponseDTO;
import com.streamora.streamora.entity.User;

public final class UserMapper {

	private UserMapper() {
	}

	public static UserResponseDTO toResponse(User user) {
		return new UserResponseDTO(
			user.getUuid(),
			user.getFullname(),
			user.getNickname(),
			user.getUsername()
		);
	}

	public static User toEntity(UserRequestDTO request) {
		User user = new User();
		user.setFullname(request.fullname());
		user.setNickname(request.nickname());
		user.setUsername(request.username());
		user.setPassword(request.password());

		return user;
	}
}
