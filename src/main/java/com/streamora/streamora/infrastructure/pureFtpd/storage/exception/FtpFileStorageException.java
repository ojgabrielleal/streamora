package com.streamora.streamora.infrastructure.pureFtpd.storage.exception;

public class FtpFileStorageException extends RuntimeException {

	public FtpFileStorageException(String message) {
		super(message);
	}

	public FtpFileStorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
