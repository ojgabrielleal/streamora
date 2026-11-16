package com.streamora.streamora.infrastructure.liquidsoap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapFileException;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapProperties;

class M3uContentGeneratorTests {

	@TempDir
	Path temporaryDirectory;

	private Path mediaDirectory;
	private M3uContentGenerator generator;

	@BeforeEach
	void setUp() throws IOException {
		mediaDirectory = Files.createDirectory(temporaryDirectory.resolve("media"));
		LiquidsoapProperties properties = new LiquidsoapProperties(
			mediaDirectory.toString(),
			temporaryDirectory.resolve("m3u").toString(),
			temporaryDirectory.resolve("config").toString(),
			temporaryDirectory.resolve("control.sock").toString(),
			100,
			500,
			10
		);
		generator = new M3uContentGenerator(new LiquidsoapPathValidator(properties));
	}

	@Test
	void generatesM3uFromOneMediaPath() throws IOException {
		Path mediaPath = Files.writeString(mediaDirectory.resolve("opening.mp3"), "audio");

		String content = generator.fromMedia("opening.mp3");

		assertThat(content).isEqualTo("#EXTM3U\n" + mediaPath + "\n");
	}

	@Test
	void generatesM3uFromMixedMediaPaths() throws IOException {
		Path mp3Path = Files.writeString(mediaDirectory.resolve("music.mp3"), "audio");
		Path flacPath = Files.writeString(mediaDirectory.resolve("music.flac"), "audio");

		String content = generator.fromMediaPaths(List.of("music.mp3", "music.flac"));

		assertThat(content).contains(mp3Path.toString(), flacPath.toString());
	}

	@Test
	void generatesM3uFromDirectoryWithoutFilteringExtensions() throws IOException {
		Files.writeString(mediaDirectory.resolve("a.ogg"), "audio");
		Path nestedDirectory = Files.createDirectory(mediaDirectory.resolve("nested"));
		Files.writeString(nestedDirectory.resolve("b.custom"), "audio");

		String content = generator.fromDirectory(mediaDirectory.toString());

		assertThat(content).contains("a.ogg", "b.custom");
	}

	@Test
	void rejectsPathOutsideSharedStorage() throws IOException {
		Path externalMedia = Files.writeString(temporaryDirectory.resolve("external.mp3"), "audio");

		assertThatThrownBy(() -> generator.fromMedia(externalMedia.toString()))
			.isInstanceOf(LiquidsoapFileException.class)
			.hasMessage("Media path must remain inside the shared storage");
	}

	@Test
	void generatesM3uFromRelativeMediaPaths() throws IOException {
		Path firstMedia = Files.writeString(mediaDirectory.resolve("first.mp3"), "audio");
		Path secondMedia = Files.writeString(mediaDirectory.resolve("second.flac"), "audio");

		String content = generator.fromRelativeMediaPaths(List.of("first.mp3", "second.flac"));

		assertThat(content).isEqualTo(
			"#EXTM3U\n" + firstMedia + "\n" + secondMedia + "\n"
		);
	}

	@Test
	void rejectsAbsolutePathForRelativeMediaGeneration() throws IOException {
		Path mediaPath = Files.writeString(mediaDirectory.resolve("track.mp3"), "audio");

		assertThatThrownBy(() -> generator.fromRelativeMediaPaths(List.of(mediaPath.toString())))
			.isInstanceOf(LiquidsoapFileException.class)
			.hasMessage("Media path must be relative to the shared storage");
	}

	@Test
	void rejectsEmptyRelativeMediaList() {
		assertThatThrownBy(() -> generator.fromRelativeMediaPaths(List.of()))
			.isInstanceOf(LiquidsoapFileException.class)
			.hasMessage("At least one media path is required");
	}

	@Test
	void rejectsNullRelativeMediaList() {
		assertThatThrownBy(() -> generator.fromRelativeMediaPaths(null))
			.isInstanceOf(LiquidsoapFileException.class)
			.hasMessage("At least one media path is required");
	}

	@Test
	void rejectsNullRelativeMediaItem() {
		assertThatThrownBy(() -> generator.fromRelativeMediaPaths(java.util.Arrays.asList((String) null)))
			.isInstanceOf(LiquidsoapFileException.class)
			.hasMessage("Media path must not be blank or contain line breaks");
	}

	@Test
	void rejectsBlankRelativeMediaItem() {
		assertThatThrownBy(() -> generator.fromRelativeMediaPaths(List.of(" ")))
			.isInstanceOf(LiquidsoapFileException.class)
			.hasMessage("Media path must not be blank or contain line breaks");
	}

	@Test
	void rejectsDirectoryAsRelativeMediaItem() throws IOException {
		Files.createDirectory(mediaDirectory.resolve("rock"));

		assertThatThrownBy(() -> generator.fromRelativeMediaPaths(List.of("rock")))
			.isInstanceOf(LiquidsoapFileException.class)
			.hasMessage("Media path must be a readable regular file");
	}

	@Test
	void rejectsSymbolicLinkAsRelativeMediaItem() throws IOException {
		Path mediaPath = Files.writeString(mediaDirectory.resolve("track.mp3"), "audio");
		Files.createSymbolicLink(mediaDirectory.resolve("link.mp3"), mediaPath);

		assertThatThrownBy(() -> generator.fromRelativeMediaPaths(List.of("link.mp3")))
			.isInstanceOf(LiquidsoapFileException.class)
			.hasMessage("Symbolic links are not allowed in media paths");
	}
}
