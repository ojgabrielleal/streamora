package com.streamora.streamora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamora.streamora.entity.User;
import com.streamora.streamora.repository.UserRepository;

@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public List<User> index() {
		return userRepository.findAll();
	}

	@Transactional(readOnly = true)
	public User show(String uuid) {
		return findByUuid(uuid);
	}

	public User store(User user) {
		return userRepository.save(user);
	}

	public User update(String uuid, User user) {
		User storedUser = findByUuid(uuid);
		storedUser.setFullname(user.getFullname());
		storedUser.setNickname(user.getNickname());
		storedUser.setUsername(user.getUsername());
		storedUser.setPassword(user.getPassword());

		return userRepository.save(storedUser);
	}

	public void delete(String uuid) {
		userRepository.delete(findByUuid(uuid));
	}

	private User findByUuid(String uuid) {
		return userRepository.findByUuid(uuid)
				.orElseThrow(() -> new ResourceNotFoundException("User", uuid));
	}
}
