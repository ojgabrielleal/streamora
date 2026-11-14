package com.streamora.streamora.infrastructure.liquidsoap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapFileException;

class LiquidsoapFileWriterTests {

	@TempDir
	Path temporaryDirectory;

	@Test
	void writesM3uAndConfigurationInSeparateDirectories() throws Exception {
		Path m3uDirectory = temporaryDirectory.resolve("m3u");
		Path configDirectory = temporaryDirectory.resolve("config");
		LiquidsoapFileWriter writer = new LiquidsoapFileWriter(m3uDirectory, configDirectory);

		writer.writeM3u("playlist.m3u", "#EXTM3U\n/media.mp3\n");
		writer.writeConfiguration("schedule.liq", "radio = blank()\n");

		assertThat(Files.readString(m3uDirectory.resolve("playlist.m3u"))).contains("/media.mp3");
		assertThat(Files.readString(configDirectory.resolve("schedule.liq"))).contains("radio");
	}

	@Test
	void rejectsFileNameWithDirectory() {
		LiquidsoapFileWriter writer = new LiquidsoapFileWriter(
			temporaryDirectory.resolve("m3u"),
			temporaryDirectory.resolve("config")
		);

		assertThatThrownBy(() -> writer.writeM3u("nested/playlist.m3u", "content"))
			.isInstanceOf(LiquidsoapFileException.class);
	}
}
