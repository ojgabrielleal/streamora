package com.streamora.streamora.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.streamora.streamora.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LiquidsoapAuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public boolean authenticate(String username, String password) {
		return userRepository.findByUsername(username)
			.filter(user -> passwordEncoder.matches(password, user.getPassword()))
			.isPresent();
	}
}
