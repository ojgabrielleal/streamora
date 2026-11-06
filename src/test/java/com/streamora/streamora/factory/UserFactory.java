package com.streamora.streamora.factory;

import java.util.List;
import java.util.stream.IntStream;

import com.streamora.streamora.dto.UserRequestDTO;
import com.streamora.streamora.dto.UserResponseDTO;
import com.streamora.streamora.entity.User;

public final class UserFactory {

	private UserFactory() {}

	public static UserRequestDTO toCreate() {
		return toCreate(1).getFirst();
	}

	public static List<UserRequestDTO> toCreate(int quantity) {
		return indexes(quantity)
			.mapToObj(index -> new UserRequestDTO(
				"User Name " + index,
				"nickname" + index,
				"username" + index,
				"password" + index
			))
			.toList();
	}

	public static User toEntity() {
		return toEntity(1).getFirst();
	}

	public static List<User> toEntity(int quantity) {
		return indexes(quantity)
			.mapToObj(index -> {
				User user = new User();
				user.setId((long) index);
				user.setUuid("user-uuid-" + index);
				user.setFullname("User Name " + index);
				user.setNickname("nickname" + index);
				user.setUsername("username" + index);
				user.setPassword("password" + index);
				return user;
			})
			.toList();
	}

	public static UserResponseDTO toResponse() {
		return toResponse(1).getFirst();
	}

	public static List<UserResponseDTO> toResponse(int quantity) {
		return indexes(quantity)
			.mapToObj(index -> new UserResponseDTO(
				"user-uuid-" + index,
				"User Name " + index,
				"nickname" + index,
				"username" + index
			))
			.toList();
	}

	private static IntStream indexes(int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException("Quantity must not be negative");
		}
		return IntStream.rangeClosed(1, quantity);
	}
}
