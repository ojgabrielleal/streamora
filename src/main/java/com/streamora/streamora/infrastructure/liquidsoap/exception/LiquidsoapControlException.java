package com.streamora.streamora.infrastructure.liquidsoap.exception;

public class LiquidsoapControlException extends RuntimeException {

	public LiquidsoapControlException(String message) {
		super(message);
	}

	public LiquidsoapControlException(String message, Throwable cause) {
		super(message, cause);
	}
}
