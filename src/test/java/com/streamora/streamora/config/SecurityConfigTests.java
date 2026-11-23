package com.streamora.streamora.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.streamora.streamora.controller.AuthController;
import com.streamora.streamora.controller.FtpFileController;
import com.streamora.streamora.controller.LiquidsoapController;
import com.streamora.streamora.controller.PlaylistController;
import com.streamora.streamora.controller.UserController;
import com.streamora.streamora.entity.UserRole;
import com.streamora.streamora.filter.JwtAuthenticationFilter;
import com.streamora.streamora.service.AuthService;
import com.streamora.streamora.service.FtpFileService;
import com.streamora.streamora.service.JwtService;
import com.streamora.streamora.service.LiquidsoapAuthService;
import com.streamora.streamora.service.LiquidsoapService;
import com.streamora.streamora.service.PlaylistService;
import com.streamora.streamora.service.UserService;

@WebMvcTest({
	AuthController.class,
	FtpFileController.class,
	LiquidsoapController.class,
	PlaylistController.class,
	UserController.class
})
@Import({
	AuthCookieProperties.class,
	CorsConfig.class,
	SecurityConfig.class,
	JwtAuthenticationFilter.class
})
class SecurityConfigTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private FtpFileService ftpFileService;

	@MockitoBean
	private LiquidsoapService liquidsoapService;

	@MockitoBean
	private LiquidsoapAuthService liquidsoapAuthService;

	@MockitoBean
	private PlaylistService playlistService;

	@MockitoBean
	private JwtService jwtService;

	@Test
	void registersSecurityFilterChain() {
		assertThat(applicationContext.getBeansOfType(SecurityFilterChain.class))
			.containsOnlyKeys("securityFilterChain");
	}

	@Test
	void protectsApplicationEndpointsFromAnonymousRequests() throws Exception {
		when(userService.index())
			.thenReturn(List.of());

		mockMvc.perform(
			get("/user")
		)
		.andExpect(status().isUnauthorized());
	}

	@Test
	void protectsStorageEndpointsFromAnonymousRequests() throws Exception {
		mockMvc.perform(get("/storage"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void protectsLiquidsoapApplyEndpointFromAnonymousRequests() throws Exception {
		mockMvc.perform(post("/liquidsoap/apply"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void protectsPlaylistTrackReplacementFromAnonymousRequests() throws Exception {
		mockMvc.perform(
			put("/playlist/playlist-uuid/tracks")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"paths":["rock/first.mp3"]}
					""")
		)
		.andExpect(status().isUnauthorized());
	}

	@Test
	void allowsApplicationEndpointsWithValidAccessToken() throws Exception {
		when(userService.index())
			.thenReturn(List.of());
		when(jwtService.validateAccessTokenAndGetUsername("valid-access-token"))
			.thenReturn(Optional.of("admin"));
		when(jwtService.validateAccessTokenAndGetRole("valid-access-token"))
			.thenReturn(Optional.of(UserRole.ADMIN));

		mockMvc.perform(
			get("/user")
				.header(HttpHeaders.AUTHORIZATION, "Bearer valid-access-token")
			)
			.andExpect(status().isOk());
	}

	@Test
	void doesNotExposeAutomaticLoginForm() throws Exception {
		mockMvc.perform(get("/login"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void allowsLoginWithoutAccessToken() throws Exception {
		when(authService.auth(org.mockito.ArgumentMatchers.any()))
			.thenReturn(new com.streamora.streamora.dto.auth.AuthTokens(
				"access-token",
				"refresh-token"
			));

		mockMvc.perform(
			post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"username":"username","password":"password"}
					""")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").value("access-token"))
			.andExpect(jsonPath("$.refreshToken").doesNotExist())
			.andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString(
				"HttpOnly"
			)));
	}

	@Test
	void allowsRefreshWithoutAccessToken() throws Exception {
		when(authService.refresh("refresh-token"))
			.thenReturn(new com.streamora.streamora.dto.auth.AuthTokens(
				"new-access-token",
				"new-refresh-token"
			));

		mockMvc.perform(
			post("/auth/refresh")
				.cookie(new jakarta.servlet.http.Cookie(
					"streamora_refresh_token",
					"refresh-token"
				))
		)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").value("new-access-token"))
			.andExpect(jsonPath("$.refreshToken").doesNotExist());
	}

	@Test
	void expiresCookieAfterInvalidRefresh() throws Exception {
		when(authService.refresh(null))
			.thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED));

		mockMvc.perform(post("/auth/refresh"))
			.andExpect(status().isUnauthorized())
			.andExpect(header().string(
				HttpHeaders.SET_COOKIE,
				org.hamcrest.Matchers.containsString("Max-Age=0")
			));
	}

	@Test
	void allowsIdempotentLogoutWithoutAccessToken() throws Exception {
		mockMvc.perform(post("/auth/logout"))
			.andExpect(status().isNoContent())
			.andExpect(header().string(
				HttpHeaders.SET_COOKIE,
				org.hamcrest.Matchers.containsString("Max-Age=0")
			));
	}

	@Test
	void allowsCorsPreflightFromConfiguredFrontendOrigin() throws Exception {
		mockMvc.perform(
			options("/playlist")
				.header(HttpHeaders.ORIGIN, "http://localhost:5173")
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name())
				.header(
					HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
					HttpHeaders.AUTHORIZATION
				)
		)
		.andExpect(status().isOk())
		.andExpect(header().string(
			HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
			"http://localhost:5173"
		))
		.andExpect(header().string(
			HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
			HttpHeaders.AUTHORIZATION
		));
	}

	@Test
	void rejectsCorsPreflightFromUnconfiguredOrigin() throws Exception {
		mockMvc.perform(
			options("/playlist")
				.header(HttpHeaders.ORIGIN, "https://untrusted.example")
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name())
		)
		.andExpect(status().isForbidden())
		.andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}

	@Test
	void rejectsCookieAuthenticationFromUnconfiguredOrigin() throws Exception {
		mockMvc.perform(
			post("/auth/logout")
				.header(HttpHeaders.ORIGIN, "https://untrusted.example")
		)
			.andExpect(status().isForbidden())
			.andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
	}

	@Test
	void allowsCookieAuthenticationFromConfiguredOrigin() throws Exception {
		mockMvc.perform(
			post("/auth/logout")
				.header(HttpHeaders.ORIGIN, "http://localhost:5173")
		)
			.andExpect(status().isNoContent())
			.andExpect(header().string(
				HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
				"http://localhost:5173"
			))
			.andExpect(header().string(
				HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
				"true"
			));
	}
}

