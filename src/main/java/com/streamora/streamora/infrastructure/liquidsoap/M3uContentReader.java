package com.streamora.streamora.infrastructure.liquidsoap;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Component;

import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapFileException;
import com.streamora.streamora.infrastructure.liquidsoap.exception.M3uReadException;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapProperties;

@Component
public class M3uContentReader {

	private static final String HEADER = "#EXTM3U";

	private final Path mediaDirectory;
	private final Path m3uDirectory;
	private final LiquidsoapPathValidator pathValidator;

	public M3uContentReader(
		LiquidsoapProperties properties,
		LiquidsoapPathValidator pathValidator
	)
	{
		mediaDirectory = Path.of(properties.mediaDirectory()).toAbsolutePath().normalize();
		m3uDirectory = Path.of(properties.m3uDirectory()).toAbsolutePath().normalize();
		this.pathValidator = pathValidator;
	}

	public List<String> read(String m3uFileName) {
		Path m3uPath = resolveM3u(m3uFileName);
		List<String> lines = readLines(m3uPath);

		validateHeader(lines);
		List<String> tracks = lines.stream()
			.map(line -> line.trim())
			.filter(line -> !line.isEmpty())
			.filter(line -> !line.startsWith("#"))
			.map(this::relativeMediaPath)
			.toList();

		if (tracks.isEmpty()) {
			throw new M3uReadException("Associated M3U file does not contain media entries");
		}

		return tracks;
	}

	private Path resolveM3u(String m3uFileName) {
		if (m3uFileName == null || !m3uFileName.matches("[A-Za-z0-9._-]+\\.m3u")) {
			throw new M3uReadException("Associated M3U file name is invalid");
		}

		Path m3uPath;

		try {
			m3uPath = m3uDirectory.resolve(m3uFileName).normalize();
		} catch (InvalidPathException exception) {
			throw new M3uReadException("Associated M3U file name is invalid", exception);
		}

		if (Files.isSymbolicLink(m3uDirectory)
			|| !m3uPath.startsWith(m3uDirectory)
			|| Files.isSymbolicLink(m3uPath)
			|| !Files.isRegularFile(m3uPath, NOFOLLOW_LINKS)
			|| !Files.isReadable(m3uPath)) {
			throw new M3uReadException("Associated M3U file is unavailable");
		}

		return m3uPath;
	}

	private List<String> readLines(Path m3uPath) {
		try {
			return Files.readAllLines(m3uPath, StandardCharsets.UTF_8);
		} catch (IOException exception) {
			throw new M3uReadException("Associated M3U file could not be read", exception);
		}
	}

	private void validateHeader(List<String> lines) {
		String firstContentLine = lines.stream()
			.map(line -> line.trim())
			.filter(line -> !line.isEmpty())
			.findFirst()
			.orElseThrow(() -> new M3uReadException("Associated M3U file is empty"));

		if (!HEADER.equals(firstContentLine)) {
			throw new M3uReadException("Associated M3U file has an invalid header");
		}
	}

	private String relativeMediaPath(String mediaPath) {
		try {
			Path validatedPath = pathValidator.validateMediaFile(mediaPath);

			return mediaDirectory.relativize(validatedPath).toString();
		} catch (LiquidsoapFileException exception) {
			throw new M3uReadException("Associated M3U file contains an invalid media entry", exception);
		}
	}
}
