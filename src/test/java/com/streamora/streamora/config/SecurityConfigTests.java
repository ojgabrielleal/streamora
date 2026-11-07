package com.streamora.streamora.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.streamora.streamora.controller.UserController;
import com.streamora.streamora.service.UserService;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class SecurityConfigTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@Test
	void registersSecurityFilterChain() {
		assertThat(applicationContext.getBeansOfType(SecurityFilterChain.class))
			.containsOnlyKeys("securityFilterChain");
	}

	@Test
	void protectsApplicationEndpointsFromAnonymousRequests() throws Exception {
		when(userService.index()).thenReturn(List.of());

		mockMvc.perform(get("/user"))
			.andExpect(status().isForbidden());
	}

	@Test
	void doesNotExposeAutomaticLoginForm() throws Exception {
		mockMvc.perform(get("/login"))
			.andExpect(status().isForbidden());
	}
}

