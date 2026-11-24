package com.streamora.streamora.infrastructure.icecast;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamora.streamora.infrastructure.icecast.records.IcecastProperties;

@Component
public class IcecastAvailabilityChecker {

	private static final String EXPECTED_RESPONSE_FIELD = "\"icestats\"";
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

	public Optional<Integer> getListenersCount() {
		HttpRequest request = HttpRequest.newBuilder(statusUri)
			.timeout(requestTimeout)
			.GET()
			.build();

		try {
			HttpResponse<String> response = httpClient.send(
				request,
				HttpResponse.BodyHandlers.ofString()
			);

			if (response.statusCode() < 200 || response.statusCode() >= 300 || response.body() == null) {
				return Optional.empty();
			}

			JsonNode root = OBJECT_MAPPER.readTree(response.body());
			JsonNode source = root.path("icestats").path("source");

			if (source.isMissingNode() || source.isNull()) {
				return Optional.empty();
			}

			if (source.isArray()) {
				int total = 0;
				boolean found = false;

				for (JsonNode entry : source) {
					if (entry.has("listeners") && entry.get("listeners").isNumber()) {
						total += entry.get("listeners").asInt();
						found = true;
					}
				}

				return found ? Optional.of(total) : Optional.empty();
			}

			if (source.has("listeners") && source.get("listeners").isNumber()) {
				return Optional.of(source.get("listeners").asInt());
			}

			return Optional.empty();
		} catch (IOException exception) {
			return Optional.empty();
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			return Optional.empty();
		}
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
