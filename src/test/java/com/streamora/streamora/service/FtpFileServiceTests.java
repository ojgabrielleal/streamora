package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.streamora.streamora.infrastructure.pureFtpd.storage.interfaces.FtpFileStorage;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileEntry;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileType;

@ExtendWith(MockitoExtension.class)
class FtpFileServiceTests {

	@Mock
	private FtpFileStorage fileStorage;

	@InjectMocks
	private FtpFileService fileService;

	@Test
	void listsFiles() {
		List<FtpFileEntry> entries = List.of(new FtpFileEntry("audio.mp3", FtpFileType.FILE, List.of()));

		when(fileStorage.list())
			.thenReturn(entries);

		List<FtpFileEntry> listedEntries = fileService.list();

		assertThat(listedEntries).isSameAs(entries);
	}

	@Test
	void deletesFile() {
		fileService.deleteFile("audio.mp3");

		verify(fileStorage).deleteFile("audio.mp3");
	}

	@Test
	void renamesFile() {
		fileService.renameFile("audio.mp3", "renamed.mp3");

		verify(fileStorage).renameFile("audio.mp3", "renamed.mp3");
	}

	@Test
	void movesFile() {
		fileService.moveFile("audio.mp3", "archive");

		verify(fileStorage).moveFile("audio.mp3", "archive");
	}

	@Test
	void createsDirectory() {
		fileService.createDirectory("archive");

		verify(fileStorage).createDirectory("archive");
	}

	@Test
	void renamesDirectory() {
		fileService.renameDirectory("archive", "old");

		verify(fileStorage).renameDirectory("archive", "old");
	}

	@Test
	void deletesDirectory() {
		fileService.deleteDirectory("archive");

		verify(fileStorage).deleteDirectory("archive");
	}
}
