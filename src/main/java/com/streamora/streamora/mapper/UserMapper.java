package com.streamora.streamora.mapper;

import com.streamora.streamora.dto.users.UserRequestDTO;
import com.streamora.streamora.dto.users.UserResponseDTO;
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

	public static User toEntity(UserRequestDTO userRequest) {
		return toEntity(userRequest, new User());
	}

	public static User toEntity(UserRequestDTO userRequest, User user) {
		user.setFullname(userRequest.fullname());
		user.setNickname(userRequest.nickname());

		if (userRequest.username() != null) {
			user.setUsername(userRequest.username());
		}

		if (userRequest.password() != null) {
			user.setPassword(userRequest.password());
		}

		return user;
	}
}
