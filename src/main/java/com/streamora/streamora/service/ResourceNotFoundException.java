package com.streamora.streamora.service;

public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String resource, String uuid) {
		super(resource + " not found with uuid: " + uuid);
	}
}
