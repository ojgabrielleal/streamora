package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.streamora.streamora.entity.User;
import com.streamora.streamora.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LiquidsoapAuthServiceTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private SettingService settingService;

	@InjectMocks
	private LiquidsoapAuthService liquidsoapAuthService;

	@Test
	void authenticateReturnsTrueWhenCredentialsAreValid() {
		User user = new User();
		user.setUsername("locutor");
		user.setPassword("encoded-password");

		when(settingService.isLiveBroadcastEnabled())
			.thenReturn(true);
		when(userRepository.findByUsername("locutor"))
			.thenReturn(Optional.of(user));
		when(passwordEncoder.matches("raw-password", "encoded-password"))
			.thenReturn(true);

		boolean authenticated = liquidsoapAuthService.authenticate("locutor", "raw-password");

		assertThat(authenticated).isTrue();
	}

	@Test
	void authenticateReturnsFalseWhenPasswordDoesNotMatch() {
		User user = new User();
		user.setUsername("locutor");
		user.setPassword("encoded-password");

		when(settingService.isLiveBroadcastEnabled())
			.thenReturn(true);
		when(userRepository.findByUsername("locutor"))
			.thenReturn(Optional.of(user));
		when(passwordEncoder.matches("wrong-password", "encoded-password"))
			.thenReturn(false);

		boolean authenticated = liquidsoapAuthService.authenticate("locutor", "wrong-password");

		assertThat(authenticated).isFalse();
	}

	@Test
	void authenticateReturnsFalseWhenUserDoesNotExist() {
		when(settingService.isLiveBroadcastEnabled())
			.thenReturn(true);
		when(userRepository.findByUsername("unknown"))
			.thenReturn(Optional.empty());

		boolean authenticated = liquidsoapAuthService.authenticate("unknown", "raw-password");

		assertThat(authenticated).isFalse();
	}

	@Test
	void authenticateReturnsFalseWhenLiveBroadcastIsDisabled() {
		when(settingService.isLiveBroadcastEnabled())
			.thenReturn(false);

		boolean authenticated = liquidsoapAuthService.authenticate("locutor", "raw-password");

		assertThat(authenticated).isFalse();
	}
}
