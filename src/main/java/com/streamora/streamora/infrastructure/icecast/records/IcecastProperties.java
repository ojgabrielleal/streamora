package com.streamora.streamora.infrastructure.icecast.records;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "icecast")
public record IcecastProperties(
	String statusUrl,
	int statusTimeoutMillis
) {}
