package com.streamora.streamora.infrastructure.pureFtpd.user;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.streamora.streamora.infrastructure.pureFtpd.user.interfaces.FtpCommandExecutor;
import com.streamora.streamora.infrastructure.pureFtpd.user.records.PureFtpdProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PureFtpd {

	private static final Pattern SAFE_USERNAME = Pattern.compile("[A-Za-z0-9][A-Za-z0-9._-]*");

	private final FtpCommandExecutor commandExecutor;
	private final PureFtpdProperties properties;

	public void createFtpdUser(String username, String password) {
		validateCredentials(username, password);
		commandExecutor.execute(
			List.of(
				properties.command(),
				"useradd",
				username,
				"-f", properties.passwordFile(),
				"-F", properties.databaseFile(),
				"-m",
				"-u", String.valueOf(properties.uid()),
				"-g", String.valueOf(properties.gid()),
				"-d", properties.homeDirectory() + "/" + username
			),
			passwordInput(password)
		);
	}

	public void updateFtpdPassword(String username, String password) {
		validateCredentials(username, password);
		commandExecutor.execute(
			List.of(
				properties.command(),
				"passwd",
				username,
				"-f", properties.passwordFile(),
				"-F", properties.databaseFile(),
				"-m"
			),
			passwordInput(password)
		);
	}

	public void deleteFtpdUser(String username) {
		validateUsername(username);
		commandExecutor.execute(
			List.of(
				properties.command(),
				"userdel",
				username,
				"-f", properties.passwordFile(),
				"-F", properties.databaseFile(),
				"-m"
			),
			""
		);
	}

	private void validateCredentials(String username, String password) {
		validateUsername(username);

		if (password == null || password.isBlank()) {
			throw new IllegalArgumentException("FTP password must not be blank");
		}
	}

	private void validateUsername(String username) {
		if (username == null || !SAFE_USERNAME.matcher(username).matches()) {
			throw new IllegalArgumentException("FTP username contains invalid characters");
		}
	}

	private String passwordInput(String password) {
		return password + System.lineSeparator() + password + System.lineSeparator();
	}
}
