package com.streamora.streamora.infrastructure.liquidsoap;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapProperties;

@Component
public class PlaylistAvailabilityChecker {

	private final Path m3uDirectory;

	public PlaylistAvailabilityChecker(LiquidsoapProperties properties) {
		m3uDirectory = Path.of(properties.m3uDirectory()).toAbsolutePath().normalize();
	}

	public boolean isAvailable(Playlist playlist) {
		String m3uFileName = playlist.getM3uFileName();

		if (m3uFileName == null || !m3uFileName.matches("[A-Za-z0-9._-]+\\.m3u")) {
			return false;
		}

		Path m3uPath = m3uDirectory.resolve(m3uFileName).normalize();

		if (!m3uPath.getParent().equals(m3uDirectory)
			|| !Files.isRegularFile(m3uPath, NOFOLLOW_LINKS)
			|| !Files.isReadable(m3uPath)) {
			return false;
		}

		try (var lines = Files.lines(m3uPath)) {
			return lines
				.map(line -> line.trim())
				.anyMatch(line -> !line.isEmpty() && !line.startsWith("#"));
		} catch (IOException | SecurityException exception) {
			return false;
		}
	}
}
