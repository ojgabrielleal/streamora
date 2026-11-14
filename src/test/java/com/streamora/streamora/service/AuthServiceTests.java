package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.streamora.streamora.dto.auth.AuthRequestDTO;
import com.streamora.streamora.entity.User;
import com.streamora.streamora.factory.UserFactory;
import com.streamora.streamora.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private AuthService authService;

	@Test
	void authenticatesValidCredentialsAndGeneratesTokens() {
		AuthRequestDTO authRequest = new AuthRequestDTO("username1", "plain-password");
		User storedUser = UserFactory.toEntity();
		Map<String, String> generatedTokens = Map.of(
			"accessToken", "access-token",
			"refreshToken", "refresh-token"
		);

		when(userRepository.findByUsername(authRequest.username()))
			.thenReturn(Optional.of(storedUser));
		when(passwordEncoder.matches(authRequest.password(), storedUser.getPassword()))
			.thenReturn(true);
		when(jwtService.generateTokens(storedUser.getUsername()))
			.thenReturn(generatedTokens);

		Map<String, String> returnedTokens = authService.auth(authRequest);

		assertThat(returnedTokens)
			.isSameAs(generatedTokens);

		verify(jwtService).generateTokens(storedUser.getUsername());
	}

	@Test
	void rejectsUnknownUsername() {
		AuthRequestDTO authRequest = new AuthRequestDTO("unknown", "password");

		when(userRepository.findByUsername(authRequest.username()))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.auth(authRequest))
			.isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
				assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
				assertThat(exception.getReason()).isEqualTo("Invalid username or password");
			});

		verifyNoInteractions(passwordEncoder, jwtService);
	}

	@Test
	void rejectsIncorrectPassword() {
		AuthRequestDTO authRequest = new AuthRequestDTO("username1", "incorrect-password");
		User storedUser = UserFactory.toEntity();

		when(userRepository.findByUsername(authRequest.username()))
			.thenReturn(Optional.of(storedUser));
		when(passwordEncoder.matches(authRequest.password(), storedUser.getPassword()))
			.thenReturn(false);

		assertThatThrownBy(() -> authService.auth(authRequest))
			.isInstanceOfSatisfying(ResponseStatusException.class, exception ->
				assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
			);

		verifyNoInteractions(jwtService);
	}
}
