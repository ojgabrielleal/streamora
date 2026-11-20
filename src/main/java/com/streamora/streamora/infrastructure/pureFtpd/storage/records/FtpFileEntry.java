package com.streamora.streamora.infrastructure.pureFtpd.storage.records;

import java.time.Instant;
import java.util.List;

public record FtpFileEntry(
	String name,
	FtpFileType type,
	String format,
	Long size,
	Instant lastModified,
	List<FtpFileEntry> children
) {
	public FtpFileEntry(String name, FtpFileType type, List<FtpFileEntry> children) {
		this(name, type, null, null, null, children);
	}

	public FtpFileEntry {
		children = List.copyOf(children);
	}
}
