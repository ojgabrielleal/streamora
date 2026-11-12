package com.streamora.streamora.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.dto.UserRequestDTO;
import com.streamora.streamora.dto.UserResponseDTO;
import com.streamora.streamora.entity.User;
import com.streamora.streamora.factory.UserFactory;

class UserMapperTests {

	@Test
	void mapsEntityToResponseWithoutInternalIdOrPassword() {
		User user = UserFactory.toEntity();

		UserResponseDTO userResponse = UserMapper.toResponse(user);

		assertThat(userResponse.uuid()).isEqualTo(user.getUuid());
		assertThat(userResponse.fullname()).isEqualTo(user.getFullname());
		assertThat(userResponse.nickname()).isEqualTo(user.getNickname());
		assertThat(userResponse.username()).isEqualTo(user.getUsername());
		assertThat(UserResponseDTO.class.getRecordComponents())
			.extracting(component -> component.getName())
			.doesNotContain("id", "password");
	}

	@Test
	void mapsRequestToNewEntity() {
		UserRequestDTO userCreateRequest = UserFactory.toCreate();

		User user = UserMapper.toEntity(userCreateRequest);

		assertThat(user.getId()).isNull();
		assertThat(user.getUuid()).isNull();
		assertThat(user.getFullname()).isEqualTo(userCreateRequest.fullname());
		assertThat(user.getNickname()).isEqualTo(userCreateRequest.nickname());
		assertThat(user.getUsername()).isEqualTo(userCreateRequest.username());
		assertThat(user.getPassword()).isEqualTo(userCreateRequest.password());
	}

	@Test
	void mapsRequestToExistingEntityAndPreservesIdentity() {
		User user = UserFactory.toEntity();

		Long originalId = user.getId();
		String originalUuid = user.getUuid();

		UserRequestDTO userUpdateRequest = UserFactory.toCreate(2).getLast();

		User mappedUser = UserMapper.toEntity(userUpdateRequest, user);

		assertThat(mappedUser)
			.isSameAs(user);
		assertThat(mappedUser.getId()).isEqualTo(originalId);
		assertThat(mappedUser.getUuid()).isEqualTo(originalUuid);
		assertThat(mappedUser.getFullname()).isEqualTo(userUpdateRequest.fullname());
		assertThat(mappedUser.getNickname()).isEqualTo(userUpdateRequest.nickname());
		assertThat(mappedUser.getUsername()).isEqualTo(userUpdateRequest.username());
		assertThat(mappedUser.getPassword()).isEqualTo(userUpdateRequest.password());
	}

	@Test
	void preservesOptionalCredentialsWhenTheyAreAbsent() {
		User user = UserFactory.toEntity();
		String originalUsername = user.getUsername();
		String originalPassword = user.getPassword();
		UserRequestDTO partialUserUpdateRequest = new UserRequestDTO("Updated Name", "updated", null, null);

		UserMapper.toEntity(partialUserUpdateRequest, user);

		assertThat(user.getUsername()).isEqualTo(originalUsername);
		assertThat(user.getPassword()).isEqualTo(originalPassword);
	}
}
