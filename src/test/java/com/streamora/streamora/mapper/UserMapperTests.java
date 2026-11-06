package com.streamora.streamora.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.dto.UserRequestDTO;
import com.streamora.streamora.dto.UserResponseDTO;
import com.streamora.streamora.entity.User;

class UserMapperTests {

	@Test
	void mapsEntityToResponseWithoutInternalIdOrPassword() {
		User user = new User();
		user.setId(1L);
		user.setUuid("user-uuid");
		user.setFullname("User Name");
		user.setNickname("nickname");
		user.setUsername("username");
		user.setPassword("secret");

		UserResponseDTO response = UserMapper.toResponse(user);

		assertThat(response.uuid()).isEqualTo("user-uuid");
		assertThat(response.fullname()).isEqualTo("User Name");
		assertThat(response.nickname()).isEqualTo("nickname");
		assertThat(response.username()).isEqualTo("username");
		assertThat(UserResponseDTO.class.getRecordComponents())
				.extracting(component -> component.getName())
				.doesNotContain("id", "password");
	}

	@Test
	void mapsRequestToNewEntity() {
		UserRequestDTO request = new UserRequestDTO("User Name", "nickname", "username", "secret");

		User user = UserMapper.toEntity(request);

		assertThat(user.getId()).isNull();
		assertThat(user.getUuid()).isNull();
		assertThat(user.getFullname()).isEqualTo("User Name");
		assertThat(user.getNickname()).isEqualTo("nickname");
		assertThat(user.getUsername()).isEqualTo("username");
		assertThat(user.getPassword()).isEqualTo("secret");
	}

	@Test
	void mapsRequestToExistingEntityAndPreservesIdentity() {
		User user = new User();
		user.setId(1L);
		user.setUuid("user-uuid");
		user.setFullname("Old name");
		user.setNickname("old nickname");
		user.setUsername("old username");
		user.setPassword("old password");
		UserRequestDTO request = new UserRequestDTO(
				"New name",
				"new nickname",
				"new username",
				"new password"
		);

		User mappedUser = UserMapper.toEntity(request, user);

		assertThat(mappedUser).isSameAs(user);
		assertThat(mappedUser.getId()).isEqualTo(1L);
		assertThat(mappedUser.getUuid()).isEqualTo("user-uuid");
		assertThat(mappedUser.getFullname()).isEqualTo("New name");
		assertThat(mappedUser.getNickname()).isEqualTo("new nickname");
		assertThat(mappedUser.getUsername()).isEqualTo("new username");
		assertThat(mappedUser.getPassword()).isEqualTo("new password");
	}
}
