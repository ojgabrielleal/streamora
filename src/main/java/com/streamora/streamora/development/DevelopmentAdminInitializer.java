package com.streamora.streamora.development;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.streamora.streamora.dto.users.UserRequestDTO;
import com.streamora.streamora.entity.UserRole;
import com.streamora.streamora.repository.UserRepository;
import com.streamora.streamora.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "environment", havingValue = "dev")
@RequiredArgsConstructor
public class DevelopmentAdminInitializer implements ApplicationRunner {

	static final String ADMIN_USERNAME = "admin";
	static final String ADMIN_PASSWORD = "admin";

	private final UserRepository userRepository;
	private final UserService userService;

	@Override
	public void run(ApplicationArguments applicationArguments) {
		if (userRepository.findByUsername(ADMIN_USERNAME).isPresent()) {
			return;
		}

		UserRequestDTO adminCreateRequest = new UserRequestDTO(
			"Streamora Administrator",
			"Administrator",
			ADMIN_USERNAME,
			ADMIN_PASSWORD,
			UserRole.ADMIN
		);

		userService.store(adminCreateRequest);
	}
}
