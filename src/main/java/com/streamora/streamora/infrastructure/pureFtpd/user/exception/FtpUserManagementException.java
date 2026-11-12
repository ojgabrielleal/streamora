package com.streamora.streamora.infrastructure.pureFtpd.user.exception;

public class FtpUserManagementException extends RuntimeException {

	public FtpUserManagementException(String message) {
		super(message);
	}

	public FtpUserManagementException(String message, Throwable cause) {
		super(message, cause);
	}
}
