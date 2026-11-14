package com.streamora.streamora.infrastructure.liquidsoap.exception;

public class LiquidsoapFileException extends RuntimeException {

	public LiquidsoapFileException(String message) {
		super(message);
	}

	public LiquidsoapFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
