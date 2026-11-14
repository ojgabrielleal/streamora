package com.streamora.streamora.infrastructure.liquidsoap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapFileException;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapProperties;

@Component
public class LiquidsoapFileWriter {

	private final Path m3uDirectory;
	private final Path configDirectory;

	@Autowired
	public LiquidsoapFileWriter(LiquidsoapProperties properties) {
		this(
			Path.of(properties.m3uDirectory()),
			Path.of(properties.configDirectory())
		);
	}

	LiquidsoapFileWriter(Path m3uDirectory, Path configDirectory) {
		this.m3uDirectory = m3uDirectory.toAbsolutePath().normalize();
		this.configDirectory = configDirectory.toAbsolutePath().normalize();
	}

	public void writeM3u(String fileName, String content) {
		validateFileName(fileName, ".m3u");
		write(m3uDirectory, fileName, content);
	}

	public void writeConfiguration(String fileName, String content) {
		validateFileName(fileName, ".liq");
		write(configDirectory, fileName, content);
	}

	private void validateFileName(String fileName, String extension) {
		if (fileName == null || fileName.isBlank()
			|| !fileName.endsWith(extension)
			|| !Path.of(fileName).getFileName().toString().equals(fileName)) {
			throw new LiquidsoapFileException("Invalid Liquidsoap file name");
		}
	}

	private void write(Path directory, String fileName, String content) {
		Path temporaryFile = null;

		try {
			Files.createDirectories(directory);
			temporaryFile = Files.createTempFile(directory, fileName, ".tmp");
			Files.writeString(temporaryFile, content, StandardCharsets.UTF_8);
			move(temporaryFile, directory.resolve(fileName));
		} catch (IOException exception) {
			throw new LiquidsoapFileException("Could not write Liquidsoap file", exception);
		} finally {
			deleteTemporaryFile(temporaryFile);
		}
	}

	private void move(Path source, Path target) throws IOException {
		try {
			Files.move(
				source,
				target,
				StandardCopyOption.ATOMIC_MOVE,
				StandardCopyOption.REPLACE_EXISTING
			);
		} catch (AtomicMoveNotSupportedException exception) {
			Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private void deleteTemporaryFile(Path temporaryFile) {
		if (temporaryFile == null) {
			return;
		}

		try {
			Files.deleteIfExists(temporaryFile);
		} catch (IOException exception) {
			// The destination write result must not be hidden by best-effort cleanup.
		}
	}
}
