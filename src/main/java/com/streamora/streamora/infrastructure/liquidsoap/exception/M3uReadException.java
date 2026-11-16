package com.streamora.streamora.infrastructure.liquidsoap.exception;

public class M3uReadException extends RuntimeException {

	public M3uReadException(String message) {
		super(message);
	}

	public M3uReadException(String message, Throwable cause) {
		super(message, cause);
	}
}
