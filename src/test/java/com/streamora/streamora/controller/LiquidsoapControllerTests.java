package com.streamora.streamora.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.streamora.streamora.config.SecurityConfig;
import com.streamora.streamora.filter.JwtAuthenticationFilter;
import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapApplyInProgressException;
import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapControlException;
import com.streamora.streamora.service.JwtService;
import com.streamora.streamora.service.LiquidsoapService;

@WebMvcTest(LiquidsoapController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class LiquidsoapControllerTests {

	private static final String ACCESS_TOKEN = "valid-access-token";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private LiquidsoapService liquidsoapService;

	@MockitoBean
	private JwtService jwtService;

	@BeforeEach
	void acceptAccessToken() {
		when(jwtService.validateAccessToken(ACCESS_TOKEN))
			.thenReturn(true);
	}

	@Test
	void appliesLiquidsoapConfiguration() throws Exception {
		mockMvc.perform(
			post("/liquidsoap/apply")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
		)
		.andExpect(status().isNoContent())
		.andExpect(content().string(""));

		verify(liquidsoapService).applyConfiguration();
	}

	@Test
	void returnsConflictWhenConfigurationApplicationIsInProgress() throws Exception {
		doThrow(new LiquidsoapApplyInProgressException())
			.when(liquidsoapService)
			.applyConfiguration();

		mockMvc.perform(
			post("/liquidsoap/apply")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
		)
		.andExpect(status().isConflict())
		.andExpect(jsonPath("$.message")
			.value("Liquidsoap configuration application is already in progress"))
		.andExpect(jsonPath("$.cause").doesNotExist())
		.andExpect(jsonPath("$.stackTrace").doesNotExist());
	}

	@Test
	void returnsServiceUnavailableWhenLiquidsoapControlFails() throws Exception {
		doThrow(new LiquidsoapControlException("Liquidsoap control is unavailable"))
			.when(liquidsoapService)
			.applyConfiguration();

		mockMvc.perform(
			post("/liquidsoap/apply")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
		)
		.andExpect(status().isServiceUnavailable())
		.andExpect(jsonPath("$.message").value("Liquidsoap control is unavailable"))
		.andExpect(jsonPath("$.cause").doesNotExist())
		.andExpect(jsonPath("$.stackTrace").doesNotExist());
	}
}
