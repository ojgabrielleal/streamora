package com.streamora.streamora.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.dto.ftp.FtpFileEntryResponseDTO;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileEntry;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileType;

class FtpFileEntryMapperTests {

	@Test
	void mapsStorageTreeRecursively() {
		Instant lastModified = Instant.parse("2026-11-20T12:00:00Z");
		FtpFileEntry ftpFileEntry = new FtpFileEntry(
			"programas",
			FtpFileType.DIRECTORY,
			null,
			null,
			lastModified,
			List.of(new FtpFileEntry(
				"abertura.mp3",
				FtpFileType.FILE,
				"mp3",
				2048L,
				lastModified,
				List.of()
			))
		);

		FtpFileEntryResponseDTO ftpFileEntryResponse = FtpFileEntryMapper.toResponse(ftpFileEntry);

		assertThat(ftpFileEntryResponse.name()).isEqualTo("programas");
		assertThat(ftpFileEntryResponse.type()).isEqualTo(FtpFileType.DIRECTORY);
		assertThat(ftpFileEntryResponse.lastModified()).isEqualTo(lastModified);
		assertThat(ftpFileEntryResponse.children())
			.singleElement()
			.satisfies(child -> {
				assertThat(child.name()).isEqualTo("abertura.mp3");
				assertThat(child.type()).isEqualTo(FtpFileType.FILE);
				assertThat(child.format()).isEqualTo("mp3");
				assertThat(child.size()).isEqualTo(2048L);
				assertThat(child.lastModified()).isEqualTo(lastModified);
				assertThat(child.children()).isEmpty();
			});
	}
}
