package com.streamora.streamora.infrastructure.pureFtpd.storage.interfaces;

import java.util.List;

import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileEntry;

public interface FtpFileStorage {

	List<FtpFileEntry> list();

	void deleteFile(String path);

	void renameFile(String path, String newName);

	void moveFile(String path, String targetDirectory);

	void createDirectory(String path);

	void renameDirectory(String path, String newName);

	void deleteDirectory(String path);
}
