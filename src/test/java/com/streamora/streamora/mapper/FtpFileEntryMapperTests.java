package com.streamora.streamora.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.dto.ftp.FtpFileEntryResponseDTO;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileEntry;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileType;

class FtpFileEntryMapperTests {

	@Test
	void mapsStorageTreeRecursively() {
		FtpFileEntry ftpFileEntry = new FtpFileEntry(
			"programas",
			FtpFileType.DIRECTORY,
			List.of(new FtpFileEntry("abertura.mp3", FtpFileType.FILE, List.of()))
		);

		FtpFileEntryResponseDTO ftpFileEntryResponse = FtpFileEntryMapper.toResponse(ftpFileEntry);

		assertThat(ftpFileEntryResponse.name()).isEqualTo("programas");
		assertThat(ftpFileEntryResponse.type()).isEqualTo(FtpFileType.DIRECTORY);
		assertThat(ftpFileEntryResponse.children())
			.singleElement()
			.satisfies(child -> {
				assertThat(child.name()).isEqualTo("abertura.mp3");
				assertThat(child.type()).isEqualTo(FtpFileType.FILE);
				assertThat(child.children()).isEmpty();
			});
	}
}
