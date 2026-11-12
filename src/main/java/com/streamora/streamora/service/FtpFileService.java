package com.streamora.streamora.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.streamora.streamora.infrastructure.pureFtpd.storage.interfaces.FtpFileStorage;
import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileEntry;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FtpFileService {

	private final FtpFileStorage fileStorage;

	public List<FtpFileEntry> list() {
		return fileStorage.list();
	}

	public void deleteFile(String path) {
		fileStorage.deleteFile(path);
	}

	public void renameFile(String path, String newName) {
		fileStorage.renameFile(path, newName);
	}

	public void moveFile(String path, String targetDirectory) {
		fileStorage.moveFile(path, targetDirectory);
	}

	public void createDirectory(String path) {
		fileStorage.createDirectory(path);
	}

	public void renameDirectory(String path, String newName) {
		fileStorage.renameDirectory(path, newName);
	}

	public void deleteDirectory(String path) {
		fileStorage.deleteDirectory(path);
	}
}
