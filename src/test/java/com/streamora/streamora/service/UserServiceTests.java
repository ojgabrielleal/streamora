package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.streamora.streamora.dto.UserRequestDTO;
import com.streamora.streamora.dto.UserResponseDTO;
import com.streamora.streamora.entity.User;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.factory.UserFactory;
import com.streamora.streamora.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Test
	void indexesUsers() {
		List<User> userEntities = UserFactory.toEntity(2);

		when(userRepository.findAll())
			.thenReturn(userEntities);

		assertThat(userService.index())
			.extracting(user -> user.uuid())
			.containsExactly(userEntities.getFirst().getUuid(), userEntities.getLast().getUuid());
	}

	@Test
	void showsUserByUuid() {
		User userEntity = UserFactory.toEntity();

		when(userRepository.findByUuid(userEntity.getUuid()))
			.thenReturn(Optional.of(userEntity));

		assertThat(userService.show(userEntity.getUuid()).uuid()).isEqualTo(userEntity.getUuid());
	}

	@Test
	void storesUser() {
		UserRequestDTO userCreateRequest = UserFactory.toCreate();

		when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
			.thenAnswer(invocation -> {
				User userToPersist = invocation.getArgument(0);
				assertThat(userToPersist.getUuid()).isNull();
				userToPersist.setUuid("generated-by-persistence");
				return userToPersist;
			});

		UserResponseDTO createdUser = userService.store(userCreateRequest);

		assertThat(createdUser.uuid()).isEqualTo("generated-by-persistence");
		assertThat(createdUser.fullname()).isEqualTo(userCreateRequest.fullname());
	}

	@Test
	void updatesEditableFieldsAndPreservesIdentity() {
		User storedUser = UserFactory.toEntity();
		UserRequestDTO userUpdateRequest = UserFactory.toCreate(2).getLast();
		Long originalId = storedUser.getId();

		when(userRepository.findByUuid(storedUser.getUuid()))
			.thenReturn(Optional.of(storedUser));

		when(userRepository.save(storedUser))
			.thenReturn(storedUser);

		UserResponseDTO updatedUser = userService.update(storedUser.getUuid(), userUpdateRequest);

		assertThat(updatedUser.uuid()).isEqualTo(storedUser.getUuid());
		assertThat(updatedUser.fullname()).isEqualTo(userUpdateRequest.fullname());
		assertThat(updatedUser.nickname()).isEqualTo(userUpdateRequest.nickname());
		assertThat(updatedUser.username()).isEqualTo(userUpdateRequest.username());
		assertThat(storedUser.getId()).isEqualTo(originalId);
		assertThat(storedUser.getPassword()).isEqualTo(userUpdateRequest.password());
	}

	@Test
	void deletesUserByUuid() {
		User userToDelete = UserFactory.toEntity();

		when(userRepository.findByUuid(userToDelete.getUuid()))
			.thenReturn(Optional.of(userToDelete));

		userService.delete(userToDelete.getUuid());

		verify(userRepository).delete(userToDelete);
	}

	@Test
	void rejectsUnknownUser() {
		when(userRepository.findByUuid("unknown"))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.show("unknown"))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("User not found with uuid: unknown");
	}
}
