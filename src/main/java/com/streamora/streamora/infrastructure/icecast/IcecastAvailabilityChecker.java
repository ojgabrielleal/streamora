package com.streamora.streamora.infrastructure.icecast;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.streamora.streamora.infrastructure.icecast.records.IcecastProperties;

@Component
public class IcecastAvailabilityChecker {

	private static final String EXPECTED_RESPONSE_FIELD = "\"icestats\"";

	private final HttpClient httpClient;
	private final URI statusUri;
	private final Duration requestTimeout;

	@Autowired
	public IcecastAvailabilityChecker(IcecastProperties properties) {
		this(
			HttpClient.newBuilder()
				.connectTimeout(Duration.ofMillis(properties.statusTimeoutMillis()))
				.build(),
			properties
		);
	}

	IcecastAvailabilityChecker(HttpClient httpClient, IcecastProperties properties) {
		this.httpClient = httpClient;
		statusUri = URI.create(properties.statusUrl());
		requestTimeout = Duration.ofMillis(properties.statusTimeoutMillis());
	}

	public boolean isAvailable() {
		HttpRequest request = HttpRequest.newBuilder(statusUri)
			.timeout(requestTimeout)
			.GET()
			.build();

		try {
			HttpResponse<String> response = httpClient.send(
				request,
				HttpResponse.BodyHandlers.ofString()
			);

			return response.statusCode() >= 200
				&& response.statusCode() < 300
				&& response.body() != null
				&& response.body().contains(EXPECTED_RESPONSE_FIELD);
		} catch (IOException exception) {
			return false;
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			return false;
		}
	}
}
