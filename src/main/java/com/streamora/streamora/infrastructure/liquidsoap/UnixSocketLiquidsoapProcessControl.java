package com.streamora.streamora.infrastructure.liquidsoap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;

import org.springframework.stereotype.Component;

import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapControlException;
import com.streamora.streamora.infrastructure.liquidsoap.interfaces.LiquidsoapProcessControl;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapProperties;

@Component
public class UnixSocketLiquidsoapProcessControl implements LiquidsoapProcessControl {

	private static final String RESTART_COMMAND = "streamora.restart\n";
	private static final String HEALTH_COMMAND = "version\n";
	private static final String RESPONSE_TERMINATOR = "END\n";

	private final UnixDomainSocketAddress socketAddress;
	private final Duration connectionTimeout;
	private final Duration recoveryTimeout;
	private final Duration retryInterval;

	public UnixSocketLiquidsoapProcessControl(LiquidsoapProperties properties) {
		socketAddress = UnixDomainSocketAddress.of(
			Path.of(properties.controlSocketPath()).toAbsolutePath().normalize()
		);
		connectionTimeout = Duration.ofMillis(properties.controlConnectionTimeoutMillis());
		recoveryTimeout = Duration.ofMillis(properties.controlRecoveryTimeoutMillis());
		retryInterval = Duration.ofMillis(properties.controlRetryIntervalMillis());
	}

	@Override
	public void restartAndAwaitRecovery() {
		sendRestartCommand();
		awaitRecovery();
	}

	private void sendRestartCommand() {
		try (SocketChannel channel = connect(connectionTimeout)) {
			write(channel, RESTART_COMMAND, connectionTimeout);
			String response = readResponse(channel, connectionTimeout);

			if (!response.isEmpty() && !response.contains("Restarting")) {
				throw new LiquidsoapControlException("Liquidsoap did not accept the restart command");
			}
		} catch (IOException exception) {
			throw new LiquidsoapControlException("Liquidsoap control is unavailable", exception);
		}
	}

	private void awaitRecovery() {
		Instant deadline = Instant.now().plus(recoveryTimeout);
		pauseBeforeRetry(deadline);

		while (Instant.now().isBefore(deadline)) {
			if (isAvailable(deadline)) {
				return;
			}

			pauseBeforeRetry(deadline);
		}

		throw new LiquidsoapControlException("Liquidsoap did not recover within the configured timeout");
	}

	private boolean isAvailable(Instant recoveryDeadline) {
		Duration attemptTimeout = shorterDuration(
			connectionTimeout,
			Duration.between(Instant.now(), recoveryDeadline)
		);

		try (SocketChannel channel = connect(attemptTimeout)) {
			write(channel, HEALTH_COMMAND, attemptTimeout);
			String response = readResponse(channel, attemptTimeout);

			return response.endsWith(RESPONSE_TERMINATOR);
		} catch (IOException exception) {
			return false;
		}
	}

	private SocketChannel connect(Duration timeout) throws IOException {
		SocketChannel channel = SocketChannel.open(StandardProtocolFamily.UNIX);
		channel.configureBlocking(false);

		try {
			if (!channel.connect(socketAddress)) {
				awaitReady(channel, SelectionKey.OP_CONNECT, timeout);
				if (!channel.finishConnect()) {
					throw new IOException("Liquidsoap control connection was not completed");
				}
			}

			return channel;
		} catch (IOException exception) {
			channel.close();
			throw exception;
		}
	}

	private void write(SocketChannel channel, String command, Duration timeout) throws IOException {
		ByteBuffer commandBuffer = StandardCharsets.UTF_8.encode(command);
		Instant deadline = Instant.now().plus(timeout);

		while (commandBuffer.hasRemaining()) {
			if (channel.write(commandBuffer) == 0) {
				awaitReady(channel, SelectionKey.OP_WRITE, remaining(deadline));
			}
		}
	}

	private String readResponse(SocketChannel channel, Duration timeout) throws IOException {
		ByteArrayOutputStream response = new ByteArrayOutputStream();
		ByteBuffer responseBuffer = ByteBuffer.allocate(256);
		Instant deadline = Instant.now().plus(timeout);

		while (!response.toString(StandardCharsets.UTF_8).endsWith(RESPONSE_TERMINATOR)) {
			int bytesRead = channel.read(responseBuffer);

			if (bytesRead < 0) {
				break;
			}

			if (bytesRead == 0) {
				awaitReady(channel, SelectionKey.OP_READ, remaining(deadline));
				continue;
			}

			responseBuffer.flip();
			response.write(responseBuffer.array(), 0, responseBuffer.remaining());
			responseBuffer.clear();
		}

		return response.toString(StandardCharsets.UTF_8);
	}

	private void awaitReady(SocketChannel channel, int operation, Duration timeout) throws IOException {
		if (timeout.isZero() || timeout.isNegative()) {
			throw new IOException("Liquidsoap control operation timed out");
		}

		try (Selector selector = Selector.open()) {
			channel.register(selector, operation);
			int readyChannels = selector.select(Math.max(1, timeout.toMillis()));

			if (readyChannels == 0) {
				throw new IOException("Liquidsoap control operation timed out");
			}

			Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
			while (selectedKeys.hasNext()) {
				selectedKeys.next();
				selectedKeys.remove();
			}
		}
	}

	private void pauseBeforeRetry(Instant deadline) {
		Duration pause = shorterDuration(retryInterval, Duration.between(Instant.now(), deadline));

		if (pause.isZero() || pause.isNegative()) {
			return;
		}

		try {
			Thread.sleep(pause);
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new LiquidsoapControlException("Liquidsoap recovery wait was interrupted", exception);
		}
	}

	private Duration remaining(Instant deadline) throws IOException {
		Duration remaining = Duration.between(Instant.now(), deadline);

		if (remaining.isZero() || remaining.isNegative()) {
			throw new IOException("Liquidsoap control operation timed out");
		}

		return remaining;
	}

	private Duration shorterDuration(Duration first, Duration second) {
		return first.compareTo(second) <= 0 ? first : second;
	}
}
