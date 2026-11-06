package com.streamora.streamora.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.RequestMapping;

import com.streamora.streamora.dto.UserRequestDTO;
import com.streamora.streamora.dto.UserResponseDTO;
import com.streamora.streamora.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTests {

	@Mock
	private UserService userService;

	private UserController userController;

	@BeforeEach
	void setUp() {
		userController = new UserController(userService);
	}

	@Test
	void usesUserPrefix() {
		assertThat(UserController.class.getAnnotation(RequestMapping.class).value())
				.containsExactly("/user");
	}

	@Test
	void delegatesCrudOperationsToService() {
		UserRequestDTO request = new UserRequestDTO("Name", "nickname", "username", "password");
		UserResponseDTO response = new UserResponseDTO("user-uuid", "Name", "nickname", "username");
		List<UserResponseDTO> responses = List.of(response);
		when(userService.index()).thenReturn(responses);
		when(userService.show("user-uuid")).thenReturn(response);
		when(userService.store(request)).thenReturn(response);
		when(userService.update("user-uuid", request)).thenReturn(response);

		assertThat(userController.index()).isSameAs(responses);
		assertThat(userController.show("user-uuid")).isSameAs(response);
		assertThat(userController.store(request)).isSameAs(response);
		assertThat(userController.update("user-uuid", request)).isSameAs(response);
		userController.delete("user-uuid");

		verify(userService).index();
		verify(userService).show("user-uuid");
		verify(userService).store(request);
		verify(userService).update("user-uuid", request);
		verify(userService).delete("user-uuid");
	}
}
