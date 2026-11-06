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

		UserResponseDTO response = UserMapper.toResponse(user);

		assertThat(response.uuid()).isEqualTo(user.getUuid());
		assertThat(response.fullname()).isEqualTo(user.getFullname());
		assertThat(response.nickname()).isEqualTo(user.getNickname());
		assertThat(response.username()).isEqualTo(user.getUsername());
		assertThat(UserResponseDTO.class.getRecordComponents())
			.extracting(component -> component.getName())
			.doesNotContain("id", "password");
	}

	@Test
	void mapsRequestToNewEntity() {
		UserRequestDTO request = UserFactory.toCreate();

		User user = UserMapper.toEntity(request);

		assertThat(user.getId()).isNull();
		assertThat(user.getUuid()).isNull();
		assertThat(user.getFullname()).isEqualTo(request.fullname());
		assertThat(user.getNickname()).isEqualTo(request.nickname());
		assertThat(user.getUsername()).isEqualTo(request.username());
		assertThat(user.getPassword()).isEqualTo(request.password());
	}

	@Test
	void mapsRequestToExistingEntityAndPreservesIdentity() {
		User user = UserFactory.toEntity();

		Long originalId = user.getId();
		String originalUuid = user.getUuid();

		UserRequestDTO request = UserFactory.toCreate(2).getLast();

		User mappedUser = UserMapper.toEntity(request, user);

		assertThat(mappedUser).isSameAs(user);
		assertThat(mappedUser.getId()).isEqualTo(originalId);
		assertThat(mappedUser.getUuid()).isEqualTo(originalUuid);
		assertThat(mappedUser.getFullname()).isEqualTo(request.fullname());
		assertThat(mappedUser.getNickname()).isEqualTo(request.nickname());
		assertThat(mappedUser.getUsername()).isEqualTo(request.username());
		assertThat(mappedUser.getPassword()).isEqualTo(request.password());
	}
}
