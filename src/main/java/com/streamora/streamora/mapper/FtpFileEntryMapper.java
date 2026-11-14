package com.streamora.streamora.mapper;

import com.streamora.streamora.dto.ftp.FtpFileEntryResponseDTO;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileEntry;

public final class FtpFileEntryMapper {

	private FtpFileEntryMapper() {
	}

	public static FtpFileEntryResponseDTO toResponse(FtpFileEntry ftpFileEntry) {
		return new FtpFileEntryResponseDTO(
			ftpFileEntry.name(),
			ftpFileEntry.type(),
			ftpFileEntry.children().stream()
				.map(FtpFileEntryMapper::toResponse)
				.toList()
		);
	}
}
