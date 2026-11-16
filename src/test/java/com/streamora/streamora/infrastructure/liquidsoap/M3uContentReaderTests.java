package com.streamora.streamora.infrastructure.liquidsoap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.streamora.streamora.infrastructure.liquidsoap.exception.M3uReadException;
import com.streamora.streamora.infrastructure.liquidsoap.records.LiquidsoapProperties;

class M3uContentReaderTests {

	@TempDir
	Path temporaryDirectory;

	private Path mediaDirectory;
	private Path m3uDirectory;
	private M3uContentReader reader;

	@BeforeEach
	void setUp() throws Exception {
		mediaDirectory = Files.createDirectory(temporaryDirectory.resolve("media"));
		m3uDirectory = Files.createDirectory(temporaryDirectory.resolve("m3u"));
		LiquidsoapProperties properties = properties();
		reader = new M3uContentReader(properties, new LiquidsoapPathValidator(properties));
	}

	@Test
	void readsRelativeTracksAndIgnoresCommentsAndBlankLines() throws Exception {
		Path rockDirectory = Files.createDirectory(mediaDirectory.resolve("rock"));
		Path firstTrack = Files.writeString(rockDirectory.resolve("first.mp3"), "audio");
		Path secondTrack = Files.writeString(rockDirectory.resolve("second.flac"), "audio");
		Files.writeString(
			m3uDirectory.resolve("playlist.m3u"),
			"""
				#EXTM3U
				#EXTINF:120,First track
				%s

				# A regular comment
				%s
				%s
				""".formatted(firstTrack, secondTrack, firstTrack)
		);

		List<String> tracks = reader.read("playlist.m3u");

		assertThat(tracks)
			.containsExactly("rock/first.mp3", "rock/second.flac", "rock/first.mp3");
	}

	@Test
	void rejectsMissingM3u() {
		assertThatThrownBy(() -> reader.read("missing.m3u"))
			.isInstanceOf(M3uReadException.class)
			.hasMessage("Associated M3U file is unavailable");
	}

	@Test
	void rejectsM3uWithoutHeader() throws Exception {
		Path mediaPath = Files.writeString(mediaDirectory.resolve("track.mp3"), "audio");
		Files.writeString(m3uDirectory.resolve("playlist.m3u"), mediaPath + "\n");

		assertThatThrownBy(() -> reader.read("playlist.m3u"))
			.isInstanceOf(M3uReadException.class)
			.hasMessage("Associated M3U file has an invalid header");
	}

	@Test
	void rejectsM3uWithoutMediaEntries() throws Exception {
		Files.writeString(m3uDirectory.resolve("playlist.m3u"), "#EXTM3U\n# Comment\n");

		assertThatThrownBy(() -> reader.read("playlist.m3u"))
			.isInstanceOf(M3uReadException.class)
			.hasMessage("Associated M3U file does not contain media entries");
	}

	@Test
	void rejectsM3uWithInvalidMediaEntry() throws Exception {
		Files.writeString(
			m3uDirectory.resolve("playlist.m3u"),
			"#EXTM3U\n" + temporaryDirectory.resolve("outside.mp3") + "\n"
		);

		assertThatThrownBy(() -> reader.read("playlist.m3u"))
			.isInstanceOf(M3uReadException.class)
			.hasMessage("Associated M3U file contains an invalid media entry");
	}

	@Test
	void rejectsSymbolicM3uFile() throws Exception {
		Path mediaPath = Files.writeString(mediaDirectory.resolve("track.mp3"), "audio");
		Path targetM3u = Files.writeString(
			temporaryDirectory.resolve("target.m3u"),
			"#EXTM3U\n" + mediaPath + "\n"
		);
		Files.createSymbolicLink(m3uDirectory.resolve("playlist.m3u"), targetM3u);

		assertThatThrownBy(() -> reader.read("playlist.m3u"))
			.isInstanceOf(M3uReadException.class)
			.hasMessage("Associated M3U file is unavailable");
	}

	private LiquidsoapProperties properties() {
		return new LiquidsoapProperties(
			mediaDirectory.toString(),
			m3uDirectory.toString(),
			temporaryDirectory.resolve("config").toString(),
			temporaryDirectory.resolve("control.sock").toString(),
			100,
			500,
			10
		);
	}
}
