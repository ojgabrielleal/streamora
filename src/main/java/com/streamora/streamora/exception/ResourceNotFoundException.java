package com.streamora.streamora.exception;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String resource, String uuid) {
		super(resource + " not found with uuid: " + uuid);
	}
}
