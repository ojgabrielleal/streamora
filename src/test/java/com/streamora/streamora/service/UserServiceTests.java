package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.streamora.streamora.dto.UserRequestDTO;
import com.streamora.streamora.dto.UserResponseDTO;
import com.streamora.streamora.entity.User;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.factory.UserFactory;
import com.streamora.streamora.infrastructure.pureFtpd.PureFtpd;
import com.streamora.streamora.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private PureFtpd pureFtpd;

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
		String hashedPassword = "hashed-password";

		when(passwordEncoder.encode(userCreateRequest.password()))
			.thenReturn(hashedPassword);

		when(userRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(User.class)))
			.thenAnswer(invocation -> {
				User userToPersist = invocation.getArgument(0);
				assertThat(userToPersist.getUuid()).isNull();
				assertThat(userToPersist.getPassword()).isEqualTo(hashedPassword);
				userToPersist.setUuid("generated-by-persistence");
				return userToPersist;
			});

		UserResponseDTO createdUser = userService.store(userCreateRequest);

		assertThat(createdUser.uuid()).isEqualTo("generated-by-persistence");
		assertThat(createdUser.fullname()).isEqualTo(userCreateRequest.fullname());
		verify(pureFtpd).createFtpdUser(userCreateRequest.username(), userCreateRequest.password());
	}

	@Test
	void updatesEditableFieldsAndPreservesIdentity() {
		User storedUser = UserFactory.toEntity();
		UserRequestDTO userUpdateRequest = new UserRequestDTO(
			"Updated Name",
			"updated",
			storedUser.getUsername(),
			"updated-password"
		);
		Long originalId = storedUser.getId();
		String hashedPassword = "updated-hashed-password";

		when(passwordEncoder.encode(userUpdateRequest.password()))
			.thenReturn(hashedPassword);

		when(userRepository.findByUuid(storedUser.getUuid()))
			.thenReturn(Optional.of(storedUser));

		when(userRepository.saveAndFlush(storedUser))
			.thenReturn(storedUser);

		UserResponseDTO updatedUser = userService.update(storedUser.getUuid(), userUpdateRequest);

		assertThat(updatedUser.uuid()).isEqualTo(storedUser.getUuid());
		assertThat(updatedUser.fullname()).isEqualTo(userUpdateRequest.fullname());
		assertThat(updatedUser.nickname()).isEqualTo(userUpdateRequest.nickname());
		assertThat(updatedUser.username()).isEqualTo(userUpdateRequest.username());
		assertThat(storedUser.getId()).isEqualTo(originalId);
		assertThat(storedUser.getPassword()).isEqualTo(hashedPassword);
		verify(pureFtpd).updateFtpdPassword("username1", userUpdateRequest.password());
	}

	@Test
	void preservesOptionalCredentialsWhenUpdatingWithoutThem() {
		User storedUser = UserFactory.toEntity();
		UserRequestDTO userUpdateRequest = new UserRequestDTO("Updated Name", "updated", null, null);
		String originalUsername = storedUser.getUsername();
		String originalPassword = storedUser.getPassword();

		when(userRepository.findByUuid(storedUser.getUuid()))
			.thenReturn(Optional.of(storedUser));
		when(userRepository.saveAndFlush(storedUser))
			.thenReturn(storedUser);

		userService.update(storedUser.getUuid(), userUpdateRequest);

		assertThat(storedUser.getUsername()).isEqualTo(originalUsername);
		assertThat(storedUser.getPassword()).isEqualTo(originalPassword);

		verify(passwordEncoder, org.mockito.Mockito.never()).encode(org.mockito.ArgumentMatchers.anyString());
		verifyNoInteractions(pureFtpd);
	}

	@Test
	void updatesFtpPasswordWithoutReplacingUser() {
		User storedUser = UserFactory.toEntity();
		UserRequestDTO userUpdateRequest = new UserRequestDTO("Updated Name", "updated", null, "new-password");

		when(userRepository.findByUuid(storedUser.getUuid()))
			.thenReturn(Optional.of(storedUser));
		when(passwordEncoder.encode(userUpdateRequest.password()))
			.thenReturn("new-password-hash");
		when(userRepository.saveAndFlush(storedUser))
			.thenReturn(storedUser);

		userService.update(storedUser.getUuid(), userUpdateRequest);

		verify(pureFtpd).updateFtpdPassword("username1", "new-password");
	}

	@Test
	void rejectsUsernameChangeEvenWhenPasswordIsProvided() {
		User storedUser = UserFactory.toEntity();
		UserRequestDTO userUpdateRequest = new UserRequestDTO(
			"Updated Name",
			"updated",
			"new-username",
			"new-password"
		);

		when(userRepository.findByUuid(storedUser.getUuid()))
			.thenReturn(Optional.of(storedUser));

		assertThatThrownBy(() -> userService.update(storedUser.getUuid(), userUpdateRequest))
			.isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
				assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
				assertThat(exception.getReason()).isEqualTo("Username cannot be changed");
			});

		verifyNoInteractions(passwordEncoder, pureFtpd);
	}

	@Test
	void rejectsBlankPassword() {
		User storedUser = UserFactory.toEntity();
		UserRequestDTO userUpdateRequest = new UserRequestDTO("Updated Name", "updated", null, " ");

		when(userRepository.findByUuid(storedUser.getUuid()))
			.thenReturn(Optional.of(storedUser));

		assertThatThrownBy(() -> userService.update(storedUser.getUuid(), userUpdateRequest))
			.isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
				assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
				assertThat(exception.getReason()).isEqualTo("Password must not be blank");
			});

		verifyNoInteractions(passwordEncoder, pureFtpd);
	}

	@Test
	void deletesUserByUuid() {
		User userToDelete = UserFactory.toEntity();

		when(userRepository.findByUuid(userToDelete.getUuid()))
			.thenReturn(Optional.of(userToDelete));

		userService.delete(userToDelete.getUuid());

		verify(userRepository).delete(userToDelete);
		verify(userRepository).flush();
		verify(pureFtpd).deleteFtpdUser(userToDelete.getUsername());
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
