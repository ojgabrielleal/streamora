package com.streamora.streamora.infrastructure.liquidsoap.interfaces;

public interface LiquidsoapProcessControl {

	boolean isAvailable();

	void restartAndAwaitRecovery();
}
