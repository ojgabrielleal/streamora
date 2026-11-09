package com.streamora.streamora.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamora.streamora.dto.UserRequestDTO;
import com.streamora.streamora.dto.UserResponseDTO;
import com.streamora.streamora.entity.User;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.mapper.UserMapper;
import com.streamora.streamora.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

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
		userToPersist.hashPassword(passwordEncoder);

		return UserMapper.toResponse(userRepository.save(userToPersist));
	}

	public UserResponseDTO update(String uuid, UserRequestDTO userRequest) {
		User storedUser = findByUuid(uuid);
		UserMapper.toEntity(userRequest, storedUser);
		if (userRequest.password() != null) {
			storedUser.hashPassword(passwordEncoder);
		}

		return UserMapper.toResponse(userRepository.save(storedUser));
	}

	public void delete(String uuid) {
		userRepository.delete(findByUuid(uuid));
	}

	private User findByUuid(String uuid) {
		return userRepository.findByUuid(uuid)
			.orElseThrow(() -> new ResourceNotFoundException("User", uuid));
	}
}
