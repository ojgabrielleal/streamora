package com.streamora.streamora.infrastructure.icecast;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.infrastructure.icecast.records.IcecastProperties;

class IcecastAvailabilityCheckerTests {

	private static final IcecastProperties PROPERTIES = new IcecastProperties(
		"http://icecast:8000/status-json.xsl",
		1000
	);

	private final HttpClient httpClient = mock(HttpClient.class);

	@Test
	void reportsOnlineForSuccessfulIcecastStatus() throws Exception {
		HttpResponse<String> response = response(200, "{\"icestats\":{}}");
		doReturn(response)
			.when(httpClient)
			.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
		IcecastAvailabilityChecker checker = new IcecastAvailabilityChecker(httpClient, PROPERTIES);

		boolean available = checker.isAvailable();

		assertThat(available).isTrue();
	}

	@Test
	void reportsOfflineForUnexpectedResponse() throws Exception {
		HttpResponse<String> response = response(200, "{}");
		doReturn(response)
			.when(httpClient)
			.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
		IcecastAvailabilityChecker checker = new IcecastAvailabilityChecker(httpClient, PROPERTIES);

		boolean available = checker.isAvailable();

		assertThat(available).isFalse();
	}

	@Test
	void reportsOfflineForHttpFailure() throws Exception {
		HttpResponse<String> response = response(503, "{\"icestats\":{}}");
		doReturn(response)
			.when(httpClient)
			.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
		IcecastAvailabilityChecker checker = new IcecastAvailabilityChecker(httpClient, PROPERTIES);

		boolean available = checker.isAvailable();

		assertThat(available).isFalse();
	}

	@Test
	void reportsOfflineForConnectionFailure() throws Exception {
		when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
			.thenThrow(new IOException("unavailable"));
		IcecastAvailabilityChecker checker = new IcecastAvailabilityChecker(httpClient, PROPERTIES);

		boolean available = checker.isAvailable();

		assertThat(available).isFalse();
	}

	@SuppressWarnings("unchecked")
	private HttpResponse<String> response(int statusCode, String body) {
		HttpResponse<String> response = mock(HttpResponse.class);
		when(response.statusCode()).thenReturn(statusCode);
		when(response.body()).thenReturn(body);

		return response;
	}
}
