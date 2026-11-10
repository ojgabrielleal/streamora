package com.streamora.streamora.infrastructure.pureFtpd;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Component;

import com.streamora.streamora.infrastructure.pureFtpd.exception.FtpUserManagementException;
import com.streamora.streamora.infrastructure.pureFtpd.interfaces.FtpCommandExecutor;

@Component
public class FtpSystemCommandExecutor implements FtpCommandExecutor {

	@Override
	public void execute(List<String> command, String standardInput) {
		Process process;
		try {
			process = new ProcessBuilder(command)
				.redirectErrorStream(true)
				.start();
			process.getOutputStream().write(standardInput.getBytes(StandardCharsets.UTF_8));
			process.getOutputStream().close();
			String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				throw new FtpUserManagementException("Pure-FTPd command failed: " + output.strip());
			}
		} catch (IOException exception) {
			throw new FtpUserManagementException("Unable to execute Pure-FTPd command", exception);
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new FtpUserManagementException("Pure-FTPd command was interrupted", exception);
		}
	}
}
