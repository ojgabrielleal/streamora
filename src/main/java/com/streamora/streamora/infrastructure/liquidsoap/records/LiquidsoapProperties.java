package com.streamora.streamora.infrastructure.liquidsoap.records;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "liquidsoap")
public record LiquidsoapProperties(
	String mediaDirectory,
	String m3uDirectory,
	String configDirectory
) {}
