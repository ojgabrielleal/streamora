package com.streamora.streamora.infrastructure.pureFtpd.storage.records;

import java.util.List;

public record FtpFileEntry(String name, FtpFileType type, List<FtpFileEntry> children) {

	public FtpFileEntry {
		children = List.copyOf(children);
	}
}
