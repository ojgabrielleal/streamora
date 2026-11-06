package com.streamora.streamora.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

	@InjectMocks
	private UserController userController;

	@Test
	void usesUserPrefix() {
		assertThat(UserController.class.getAnnotation(RequestMapping.class).value())
			.containsExactly("/user");
	}

	@Test
	void delegatesIndexToService() {
		List<UserResponseDTO> userResponses = List.of(userResponse());

		when(userService.index())
			.thenReturn(userResponses);

		assertThat(userController.index()).isSameAs(userResponses);

		verify(userService).index();
	}

	@Test
	void delegatesShowToService() {
		UserResponseDTO userResponse = userResponse();

		when(userService.show("user-uuid"))
			.thenReturn(userResponse);

		assertThat(userController.show("user-uuid")).isSameAs(userResponse);

		verify(userService).show("user-uuid");
	}

	@Test
	void delegatesStoreToService() {
		UserRequestDTO userRequest = userRequest();
		UserResponseDTO userResponse = userResponse();

		when(userService.store(userRequest))
			.thenReturn(userResponse);

		assertThat(userController.store(userRequest)).isSameAs(userResponse);

		verify(userService).store(userRequest);
	}

	@Test
	void delegatesUpdateToService() {
		UserRequestDTO userRequest = userRequest();
		UserResponseDTO userResponse = userResponse();

		when(userService.update("user-uuid", userRequest))
			.thenReturn(userResponse);

		assertThat(userController.update("user-uuid", userRequest)).isSameAs(userResponse);

		verify(userService).update("user-uuid", userRequest);
	}

	@Test
	void delegatesDeleteToService() {
		userController.delete("user-uuid");

		verify(userService).delete("user-uuid");
	}

	private UserRequestDTO userRequest() {
		return new UserRequestDTO("Name", "nickname", "username", "password");
	}

	private UserResponseDTO userResponse() {
		return new UserResponseDTO("user-uuid", "Name", "nickname", "username");
	}
}
