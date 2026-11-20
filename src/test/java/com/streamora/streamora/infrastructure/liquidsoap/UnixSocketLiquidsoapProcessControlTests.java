package com.streamora.streamora.infrastructure.liquidsoap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapControlException;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapProperties;

class UnixSocketLiquidsoapProcessControlTests {

	@TempDir
	Path temporaryDirectory;

	@Test
	void reportsAvailableControlForCompleteHealthResponse() throws Exception {
		Path socketPath = temporaryDirectory.resolve("control.sock");
		AtomicReference<String> healthCommand = new AtomicReference<>();
		ServerSocketChannel server = bind(socketPath);
		Thread serverThread = Thread.ofVirtual().start(() -> {
			try (server; SocketChannel connection = server.accept()) {
				healthCommand.set(readCommand(connection));
				connection.write(StandardCharsets.UTF_8.encode("2.3.3\nEND\n"));
			} catch (IOException ignored) {
			}
		});
		UnixSocketLiquidsoapProcessControl processControl = processControl(socketPath, 1000, 2000, 20);

		boolean available = processControl.isAvailable();
		serverThread.join();

		assertThat(available).isTrue();
		assertThat(healthCommand.get()).isEqualTo("version\n");
	}

	@Test
	void reportsUnavailableControlForMissingSocket() {
		Path socketPath = temporaryDirectory.resolve("missing.sock");
		UnixSocketLiquidsoapProcessControl processControl = processControl(socketPath, 50, 100, 10);

		boolean available = processControl.isAvailable();

		assertThat(available).isFalse();
	}

	@Test
	void sendsFixedRestartCommandAndWaitsForNewControlConnection() throws Exception {
		Path socketPath = temporaryDirectory.resolve("control.sock");
		AtomicReference<String> restartCommand = new AtomicReference<>();
		AtomicReference<String> healthCommand = new AtomicReference<>();
		AtomicReference<Throwable> serverFailure = new AtomicReference<>();
		ServerSocketChannel initialServer = bind(socketPath);
		Thread serverThread = Thread.ofVirtual().start(() -> {
			try {
				try (initialServer; SocketChannel restartConnection = initialServer.accept()) {
					restartCommand.set(readCommand(restartConnection));
					restartConnection.write(StandardCharsets.UTF_8.encode("Restarting\nEND\n"));
				}

				Files.deleteIfExists(socketPath);
				Thread.sleep(50);
				try (ServerSocketChannel recoveredServer = bind(socketPath);
					SocketChannel healthConnection = recoveredServer.accept()) {
					healthCommand.set(readCommand(healthConnection));
					healthConnection.write(StandardCharsets.UTF_8.encode("2.3.3\nEND\n"));
				}
			} catch (Throwable throwable) {
				serverFailure.set(throwable);
			}
		});
		UnixSocketLiquidsoapProcessControl processControl = processControl(socketPath, 1000, 2000, 20);

		processControl.restartAndAwaitRecovery();
		serverThread.join();

		assertThat(restartCommand.get()).isEqualTo("streamora.restart\n");
		assertThat(healthCommand.get()).isEqualTo("version\n");
		assertThat(serverFailure.get()).isNull();
	}

	@Test
	void reportsUnavailableControlWhenSocketDoesNotExist() {
		Path socketPath = temporaryDirectory.resolve("missing.sock");
		UnixSocketLiquidsoapProcessControl processControl = processControl(socketPath, 50, 100, 10);

		assertThatThrownBy(processControl::restartAndAwaitRecovery)
			.isInstanceOf(LiquidsoapControlException.class)
			.hasMessage("Liquidsoap control is unavailable");
	}

	@Test
	void rejectsUnexpectedRestartCommandResponse() throws Exception {
		Path socketPath = temporaryDirectory.resolve("control.sock");
		ServerSocketChannel server = bind(socketPath);
		Thread serverThread = Thread.ofVirtual().start(() -> {
			try (server; SocketChannel connection = server.accept()) {
				readCommand(connection);
				connection.write(StandardCharsets.UTF_8.encode("Unknown command\nEND\n"));
			} catch (IOException ignored) {
			}
		});
		UnixSocketLiquidsoapProcessControl processControl = processControl(socketPath, 1000, 2000, 20);

		assertThatThrownBy(processControl::restartAndAwaitRecovery)
			.isInstanceOf(LiquidsoapControlException.class)
			.hasMessage("Liquidsoap did not accept the restart command");

		serverThread.join();
	}

	private ServerSocketChannel bind(Path socketPath) throws IOException {
		ServerSocketChannel server = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
		server.bind(UnixDomainSocketAddress.of(socketPath));

		return server;
	}

	private String readCommand(SocketChannel connection) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(128);
		int bytesRead = connection.read(buffer);
		buffer.flip();

		return StandardCharsets.UTF_8.decode(buffer).toString().substring(0, bytesRead);
	}

	private UnixSocketLiquidsoapProcessControl processControl(
		Path socketPath,
		int connectionTimeoutMillis,
		int recoveryTimeoutMillis,
		int retryIntervalMillis
	)
	{
		LiquidsoapProperties properties = new LiquidsoapProperties(
			temporaryDirectory.resolve("media").toString(),
			temporaryDirectory.resolve("m3u").toString(),
			temporaryDirectory.resolve("config").toString(),
			socketPath.toString(),
			connectionTimeoutMillis,
			recoveryTimeoutMillis,
			retryIntervalMillis
		);

		return new UnixSocketLiquidsoapProcessControl(properties);
	}
}
