package com.streamora.streamora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamora.streamora.dto.UserRequestDTO;
import com.streamora.streamora.dto.UserResponseDTO;
import com.streamora.streamora.entity.User;
import com.streamora.streamora.mapper.UserMapper;
import com.streamora.streamora.repository.UserRepository;

@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

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

	public UserResponseDTO store(UserRequestDTO request) {
		User user = UserMapper.toEntity(request);

		return UserMapper.toResponse(userRepository.save(user));
	}

	public UserResponseDTO update(String uuid, UserRequestDTO request) {
		User storedUser = findByUuid(uuid);
		User changes = UserMapper.toEntity(request);
		storedUser.setFullname(changes.getFullname());
		storedUser.setNickname(changes.getNickname());
		storedUser.setUsername(changes.getUsername());
		storedUser.setPassword(changes.getPassword());

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
