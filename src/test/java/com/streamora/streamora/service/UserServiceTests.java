package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.streamora.streamora.dto.UserRequestDTO;
import com.streamora.streamora.dto.UserResponseDTO;
import com.streamora.streamora.entity.User;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

	@Mock
	private UserRepository userRepository;

	private UserService userService;

	@BeforeEach
	void setUp() {
		userService = new UserService(userRepository);
	}

	@Test
	void indexesUsers() {
		List<User> users = List.of(user("user-uuid"));
		when(userRepository.findAll()).thenReturn(users);

		assertThat(userService.index())
				.extracting(UserResponseDTO::uuid)
				.containsExactly("user-uuid");
	}

	@Test
	void showsUserByUuid() {
		User user = user("user-uuid");
		when(userRepository.findByUuid("user-uuid")).thenReturn(Optional.of(user));

		assertThat(userService.show("user-uuid").uuid()).isEqualTo("user-uuid");
	}

	@Test
	void storesUser() {
		UserRequestDTO request = request("Name");
		when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
				.thenAnswer(invocation -> {
					User user = invocation.getArgument(0);
					assertThat(user.getUuid()).isNull();
					user.setUuid("generated-by-persistence");
					return user;
				});

		UserResponseDTO response = userService.store(request);

		assertThat(response.uuid()).isEqualTo("generated-by-persistence");
		assertThat(response.fullname()).isEqualTo("Name");
	}

	@Test
	void updatesEditableFieldsAndPreservesIdentity() {
		User storedUser = user("stored-uuid");
		storedUser.setId(1L);
		UserRequestDTO changes = new UserRequestDTO(
				"New name",
				"new nickname",
				"new username",
				"new password"
		);
		when(userRepository.findByUuid("stored-uuid")).thenReturn(Optional.of(storedUser));
		when(userRepository.save(storedUser)).thenReturn(storedUser);

		UserResponseDTO updatedUser = userService.update("stored-uuid", changes);

		assertThat(updatedUser.uuid()).isEqualTo("stored-uuid");
		assertThat(updatedUser.fullname()).isEqualTo("New name");
		assertThat(updatedUser.nickname()).isEqualTo("new nickname");
		assertThat(updatedUser.username()).isEqualTo("new username");
		assertThat(storedUser.getId()).isEqualTo(1L);
		assertThat(storedUser.getPassword()).isEqualTo("new password");
	}

	@Test
	void deletesUserByUuid() {
		User user = user("user-uuid");
		when(userRepository.findByUuid("user-uuid")).thenReturn(Optional.of(user));

		userService.delete("user-uuid");

		verify(userRepository).delete(user);
	}

	@Test
	void rejectsUnknownUser() {
		when(userRepository.findByUuid("unknown")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.show("unknown"))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("User not found with uuid: unknown");
	}

	private User user(String uuid) {
		User user = new User();
		user.setUuid(uuid);
		user.setFullname("Name");
		user.setNickname("nickname");
		user.setUsername("username");
		user.setPassword("password");
		return user;
	}

	private UserRequestDTO request(String fullname) {
		return new UserRequestDTO(fullname, "nickname", "username", "password");
	}
}
