package com.streamora.streamora.infrastructure.pureFtpd.records;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pureftpd")
public record PureFtpdProperties(
	String command,
	String passwordFile,
	String databaseFile,
	String homeDirectory,
	int uid,
	int gid
) {}
