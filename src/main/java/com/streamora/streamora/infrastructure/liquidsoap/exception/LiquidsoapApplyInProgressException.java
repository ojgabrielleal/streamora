package com.streamora.streamora.infrastructure.liquidsoap.exception;

public class LiquidsoapApplyInProgressException extends RuntimeException {

	public LiquidsoapApplyInProgressException() {
		super("Liquidsoap configuration application is already in progress");
	}
}
