package com.streamora.streamora.development;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.streamora.streamora.dto.users.UserRequestDTO;
import com.streamora.streamora.entity.User;
import com.streamora.streamora.repository.UserRepository;
import com.streamora.streamora.service.UserService;

@ExtendWith(MockitoExtension.class)
class DevelopmentAdminInitializerTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserService userService;

	@InjectMocks
	private DevelopmentAdminInitializer developmentAdminInitializer;

	@Test
	void createsDevelopmentAdminWhenItDoesNotExist() {
		when(userRepository.findByUsername("admin"))
			.thenReturn(Optional.empty());

		developmentAdminInitializer.run(null);

		ArgumentCaptor<UserRequestDTO> adminCreateRequestCaptor = ArgumentCaptor.forClass(
			UserRequestDTO.class
		);
		verify(userService).store(adminCreateRequestCaptor.capture());

		assertThat(adminCreateRequestCaptor.getValue())
			.isEqualTo(new UserRequestDTO(
				"Streamora Administrator",
				"Administrator",
				"admin",
				"admin"
			));
	}

	@Test
	void preservesExistingDevelopmentAdmin() {
		User existingAdmin = new User();

		when(userRepository.findByUsername("admin"))
			.thenReturn(Optional.of(existingAdmin));

		developmentAdminInitializer.run(null);

		verifyNoInteractions(userService);
	}

	@Test
	void isEnabledForDevelopmentEnvironment() {
		conditionalContextRunner()
			.withPropertyValues("environment=dev")
			.run(context -> assertThat(context)
				.hasSingleBean(DevelopmentAdminInitializer.class)
			);
	}

	@Test
	void isDisabledOutsideDevelopmentEnvironment() {
		conditionalContextRunner()
			.withPropertyValues("environment=prod")
			.run(context -> assertThat(context)
				.doesNotHaveBean(DevelopmentAdminInitializer.class)
			);
	}

	private ApplicationContextRunner conditionalContextRunner() {
		return new ApplicationContextRunner()
			.withBean(UserRepository.class, () -> mock(UserRepository.class))
			.withBean(UserService.class, () -> mock(UserService.class))
			.withUserConfiguration(DevelopmentAdminInitializer.class);
	}
}
