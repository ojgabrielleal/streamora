package com.streamora.streamora.service;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.streamora.streamora.dto.auth.AuthRequestDTO;
import com.streamora.streamora.entity.User;
import com.streamora.streamora.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

	private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password";

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public Map<String, String> auth(AuthRequestDTO authRequest) {
		if (authRequest.username() == null || authRequest.username().isBlank()
			|| authRequest.password() == null || authRequest.password().isBlank()
		) {
			throw invalidCredentials();
		}

		User storedUser = userRepository.findByUsername(authRequest.username())
			.orElseThrow(this::invalidCredentials);

		if (!passwordEncoder.matches(authRequest.password(), storedUser.getPassword())) {
			throw invalidCredentials();
		}

		return jwtService.generateTokens(storedUser.getUsername());
	}

	private ResponseStatusException invalidCredentials() {
		return new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE);
	}
}
