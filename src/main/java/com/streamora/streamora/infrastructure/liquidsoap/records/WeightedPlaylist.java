package com.streamora.streamora.infrastructure.liquidsoap.records;

public record WeightedPlaylist(
	String uuid,
	String sourceName,
	String m3uFileName,
	int weight
) {}
