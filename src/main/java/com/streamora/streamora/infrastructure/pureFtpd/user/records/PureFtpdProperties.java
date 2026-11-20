package com.streamora.streamora.infrastructure.pureFtpd.user.records;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pureftpd")
public record PureFtpdProperties(
	String command,
	String passwordFile,
	String databaseFile,
	String homeDirectory,
	String publicHost,
	int port,
	int uid,
	int gid
) {}
