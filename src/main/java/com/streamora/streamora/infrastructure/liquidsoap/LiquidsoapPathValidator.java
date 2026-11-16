package com.streamora.streamora.infrastructure.liquidsoap;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapFileException;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapProperties;

@Component
public class LiquidsoapPathValidator {

	private final Path mediaDirectory;

	public LiquidsoapPathValidator(LiquidsoapProperties properties) {
		mediaDirectory = Path.of(properties.mediaDirectory()).toAbsolutePath().normalize();
	}

	public Path validateMediaFile(String mediaPath) {
		Path normalizedPath = resolve(mediaPath);
		validateWithinMediaDirectory(normalizedPath);
		validateExistingSegments(normalizedPath);

		if (!Files.isRegularFile(normalizedPath, NOFOLLOW_LINKS) || !Files.isReadable(normalizedPath)) {
			throw new LiquidsoapFileException("Media path must be a readable regular file");
		}

		return normalizedPath;
	}

	public Path validateRelativeMediaFile(String mediaPath) {
		if (mediaPath == null || mediaPath.isBlank()) {
			return validateMediaFile(mediaPath);
		}

		try {
			if (Path.of(mediaPath).isAbsolute()) {
				throw new LiquidsoapFileException("Media path must be relative to the shared storage");
			}
		} catch (InvalidPathException exception) {
			throw new LiquidsoapFileException("Invalid media path", exception);
		}

		return validateMediaFile(mediaPath);
	}

	public Path validateMediaDirectory(String directoryPath) {
		Path normalizedPath = resolve(directoryPath);
		validateWithinMediaDirectory(normalizedPath);
		validateExistingSegments(normalizedPath);

		if (!Files.isDirectory(normalizedPath, NOFOLLOW_LINKS) || !Files.isReadable(normalizedPath)) {
			throw new LiquidsoapFileException("Media path must be a readable directory");
		}

		return normalizedPath;
	}

	private Path resolve(String path) {
		if (path == null || path.isBlank() || path.contains("\n") || path.contains("\r")) {
			throw new LiquidsoapFileException("Media path must not be blank or contain line breaks");
		}

		try {
			Path requestedPath = Path.of(path);

			if (requestedPath.isAbsolute()) {
				return requestedPath.normalize();
			}

			return mediaDirectory.resolve(requestedPath).normalize();
		} catch (InvalidPathException exception) {
			throw new LiquidsoapFileException("Invalid media path", exception);
		}
	}

	private void validateWithinMediaDirectory(Path path) {
		if (!path.startsWith(mediaDirectory)) {
			throw new LiquidsoapFileException("Media path must remain inside the shared storage");
		}
	}

	private void validateExistingSegments(Path path) {
		if (Files.isSymbolicLink(mediaDirectory)) {
			throw new LiquidsoapFileException("Shared media directory must not be a symbolic link");
		}

		Path currentPath = mediaDirectory;
		Path relativePath = mediaDirectory.relativize(path);

		for (Path segment : relativePath) {
			currentPath = currentPath.resolve(segment);

			if (Files.isSymbolicLink(currentPath)) {
				throw new LiquidsoapFileException("Symbolic links are not allowed in media paths");
			}
		}
	}
}
