package com.streamora.streamora.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.streamora.streamora.dto.services.ServiceAvailabilityStatus;
import com.streamora.streamora.dto.services.ServiceStatusResponseDTO;
import com.streamora.streamora.filter.JwtAuthenticationFilter;
import com.streamora.streamora.service.JwtService;
import com.streamora.streamora.service.RadioServiceStatusService;

@WebMvcTest(ServiceStatusController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class ServiceStatusControllerTests {

	private static final String ACCESS_TOKEN = "valid-access-token";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RadioServiceStatusService radioServiceStatusService;

	@MockitoBean
	private JwtService jwtService;

	@BeforeEach
	void acceptAccessToken() {
		when(jwtService.validateAccessToken(ACCESS_TOKEN))
			.thenReturn(true);
	}

	@Test
	void returnsBothServiceStatuses() throws Exception {
		ServiceStatusResponseDTO serviceStatus = new ServiceStatusResponseDTO(
			ServiceAvailabilityStatus.ONLINE,
			ServiceAvailabilityStatus.OFFLINE
		);
		when(radioServiceStatusService.getStatus())
			.thenReturn(serviceStatus);

		mockMvc.perform(
			get("/services/status")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
		)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.icecast").value("ONLINE"))
		.andExpect(jsonPath("$.liquidsoap").value("OFFLINE"));

		verify(radioServiceStatusService).getStatus();
	}

	@Test
	void requiresAuthentication() throws Exception {
		mockMvc.perform(get("/services/status"))
			.andExpect(status().isUnauthorized());
	}
}
