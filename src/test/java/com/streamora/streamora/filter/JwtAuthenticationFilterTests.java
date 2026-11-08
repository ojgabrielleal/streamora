package com.streamora.streamora.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.streamora.streamora.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTests {

	@Mock
	private JwtService jwtService;

	@Mock
	private FilterChain filterChain;

	@InjectMocks
	private JwtAuthenticationFilter filter;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void rejectsInvalidAccessToken() throws ServletException, IOException {
		when(jwtService.validateAccessToken("invalid-token")).thenReturn(false);

		MockHttpServletResponse response = performRequest("Bearer invalid-token", "/user");

		assertThat(response.getStatus()).isEqualTo(401);
		verify(filterChain, never()).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
	}

	@Test
	void authenticatesRequestWithValidAccessToken() throws ServletException, IOException {
		when(jwtService.validateAccessToken("valid-token")).thenReturn(true);

		MockHttpServletRequest request = request("/user");
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");

		filter.doFilter(request, response, filterChain);

		assertThat(SecurityContextHolder.getContext().getAuthentication())
			.isNotNull()
			.satisfies(authentication -> {
				assertThat(authentication.isAuthenticated()).isTrue();
				assertThat(authentication.getPrincipal()).isEqualTo("valid-token");
			});
		verify(filterChain).doFilter(request, response);
	}

	private MockHttpServletResponse performRequest(String authorization, String path)
		throws ServletException, IOException {
		MockHttpServletRequest request = request(path);
		MockHttpServletResponse response = new MockHttpServletResponse();

		if (authorization != null) {
			request.addHeader(HttpHeaders.AUTHORIZATION, authorization);
		}

		filter.doFilter(request, response, filterChain);
		return response;
	}

	private MockHttpServletRequest request(String path) {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
		request.setServletPath(path);
		return request;
	}
}
