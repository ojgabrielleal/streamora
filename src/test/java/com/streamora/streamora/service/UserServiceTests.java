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

import com.streamora.streamora.entity.User;
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
		List<User> users = List.of(new User());
		when(userRepository.findAll()).thenReturn(users);

		assertThat(userService.index()).isSameAs(users);
	}

	@Test
	void showsUserByUuid() {
		User user = user("user-uuid");
		when(userRepository.findByUuid("user-uuid")).thenReturn(Optional.of(user));

		assertThat(userService.show("user-uuid")).isSameAs(user);
	}

	@Test
	void storesUser() {
		User user = user("user-uuid");
		when(userRepository.save(user)).thenReturn(user);

		assertThat(userService.store(user)).isSameAs(user);
	}

	@Test
	void updatesEditableFieldsAndPreservesIdentity() {
		User storedUser = user("stored-uuid");
		storedUser.setId(1L);
		User changes = user("other-uuid");
		changes.setFullname("New name");
		changes.setNickname("new nickname");
		changes.setUsername("new username");
		changes.setPassword("new password");
		when(userRepository.findByUuid("stored-uuid")).thenReturn(Optional.of(storedUser));
		when(userRepository.save(storedUser)).thenReturn(storedUser);

		User updatedUser = userService.update("stored-uuid", changes);

		assertThat(updatedUser.getId()).isEqualTo(1L);
		assertThat(updatedUser.getUuid()).isEqualTo("stored-uuid");
		assertThat(updatedUser.getFullname()).isEqualTo("New name");
		assertThat(updatedUser.getNickname()).isEqualTo("new nickname");
		assertThat(updatedUser.getUsername()).isEqualTo("new username");
		assertThat(updatedUser.getPassword()).isEqualTo("new password");
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
}
