package com.streamora.streamora.infrastructure.liquidsoap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapFileException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class M3uContentGenerator {

	private static final String HEADER = "#EXTM3U";

	private final LiquidsoapPathValidator pathValidator;

	public String fromMedia(String mediaPath) {
		return fromMediaPaths(List.of(mediaPath));
	}

	public String fromMediaPaths(List<String> mediaPaths) {
		return fromMediaPaths(mediaPaths, pathValidator::validateMediaFile);
	}

	public String fromRelativeMediaPaths(List<String> mediaPaths) {
		return fromMediaPaths(mediaPaths, pathValidator::validateRelativeMediaFile);
	}

	private String fromMediaPaths(
		List<String> mediaPaths,
		Function<String, Path> pathValidation
	)
	{
		if (mediaPaths == null || mediaPaths.isEmpty()) {
			throw new LiquidsoapFileException("At least one media path is required");
		}

		List<Path> normalizedPaths = mediaPaths.stream()
			.map(pathValidation)
			.toList();

		return content(normalizedPaths);
	}

	public String fromDirectory(String directoryPath) {
		Path normalizedDirectory = pathValidator.validateMediaDirectory(directoryPath);

		try (var mediaPaths = Files.walk(normalizedDirectory)) {
			List<Path> normalizedPaths = mediaPaths
				.filter(path -> Files.isRegularFile(path))
				.sorted(Comparator.comparing(Path::toString))
				.map(path -> pathValidator.validateMediaFile(path.toString()))
				.toList();

			if (normalizedPaths.isEmpty()) {
				throw new LiquidsoapFileException("Media directory does not contain readable files");
			}

			return content(normalizedPaths);
		} catch (IOException exception) {
			throw new LiquidsoapFileException("Could not read media directory", exception);
		}
	}

	private String content(List<Path> mediaPaths) {
		return HEADER + System.lineSeparator()
			+ String.join(
				System.lineSeparator(),
				mediaPaths.stream().map(Path::toString).toList()
			)
			+ System.lineSeparator();
	}
}
