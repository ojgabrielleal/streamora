package com.streamora.streamora.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.streamora.streamora.dto.users.UserRequestDTO;
import com.streamora.streamora.dto.users.UserResponseDTO;
import com.streamora.streamora.entity.User;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.infrastructure.pureFtpd.user.PureFtpd;
import com.streamora.streamora.mapper.UserMapper;
import com.streamora.streamora.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final PureFtpd pureFtpd;

	@Transactional(readOnly = true)
	public List<UserResponseDTO> index() {
		return userRepository.findAll().stream()
			.map(user -> UserMapper.toResponse(user))
			.toList();
	}

	@Transactional(readOnly = true)
	public UserResponseDTO show(String uuid) {
		return UserMapper.toResponse(findByUuid(uuid));
	}

	public UserResponseDTO store(UserRequestDTO userRequest) {
		User userToPersist = UserMapper.toEntity(userRequest);
		userToPersist.setPassword(passwordEncoder.encode(userRequest.password()));

		User storedUser = userRepository.saveAndFlush(userToPersist);
		pureFtpd.createFtpdUser(userRequest.username(), userRequest.password());

		return UserMapper.toResponse(storedUser);
	}

	public UserResponseDTO update(String uuid, UserRequestDTO userRequest) {
		User storedUser = findByUuid(uuid);
		String currentUsername = storedUser.getUsername();

		String requestedUsername = userRequest.username();
		String plainTextPassword = userRequest.password();

		boolean usernameChanged = requestedUsername != null && !requestedUsername.equals(currentUsername);

		if (usernameChanged) {
			throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"Username cannot be changed"
			);
		}

		if (plainTextPassword != null && plainTextPassword.isBlank()) {
			throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"Password must not be blank"
			);
		}

		UserMapper.toEntity(userRequest, storedUser);

		if (plainTextPassword != null) {
			storedUser.setPassword(passwordEncoder.encode(plainTextPassword));
		}

		User updatedUser = userRepository.saveAndFlush(storedUser);

		if (plainTextPassword != null) {
			pureFtpd.updateFtpdPassword(currentUsername, plainTextPassword);
		}

		return UserMapper.toResponse(updatedUser);
	}

	public void delete(String uuid) {
		User userToDelete = findByUuid(uuid);

		userRepository.delete(userToDelete);
		userRepository.flush();

		pureFtpd.deleteFtpdUser(userToDelete.getUsername());
	}

	private User findByUuid(String uuid) {
		return userRepository.findByUuid(uuid)
			.orElseThrow(() -> new ResourceNotFoundException("User", uuid));
	}
}
