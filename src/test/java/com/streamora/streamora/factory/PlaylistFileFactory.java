package com.streamora.streamora.factory;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.streamora.streamora.dto.PlaylistFileRequestDTO;
import com.streamora.streamora.dto.PlaylistFileResponseDTO;
import com.streamora.streamora.entity.PlaylistFile;
import com.streamora.streamora.entity.type.PlaylistFileType;

public final class PlaylistFileFactory {

	private PlaylistFileFactory() {}

	public static PlaylistFileRequestDTO toCreate() {
		return toCreate(1).getFirst();
	}

	public static List<PlaylistFileRequestDTO> toCreate(int quantity) {
		return indexes(quantity)
			.mapToObj(index -> new PlaylistFileRequestDTO(
				index % 2 == 0 ? PlaylistFileType.jingle : PlaylistFileType.music,
				"File " + index,
				"path/file-" + index + ".mp3"
			))
			.toList();
	}

	public static PlaylistFile toEntity() {
		return toEntity(1).getFirst();
	}

	public static List<PlaylistFile> toEntity(int quantity) {
		return indexes(quantity)
			.mapToObj(index -> {
				PlaylistFile file = new PlaylistFile();
				file.setId((long) index);
				file.setUuid("file-uuid-" + index);
				file.setType(index % 2 == 0 ? PlaylistFileType.jingle : PlaylistFileType.music);
				file.setName("File " + index);
				file.setPath("path/file-" + index + ".mp3");
				return file;
			})
			.toList();
	}

	public static PlaylistFileResponseDTO toResponse() {
		return toResponse(1).getFirst();
	}

	public static List<PlaylistFileResponseDTO> toResponse(int quantity) {
		return indexes(quantity)
			.mapToObj(index -> new PlaylistFileResponseDTO(
				"file-uuid-" + index,
				index % 2 == 0 ? PlaylistFileType.jingle : PlaylistFileType.music,
				"File " + index,
				"path/file-" + index + ".mp3",
				Set.of()
			))
			.toList();
	}

	private static IntStream indexes(int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException("Quantity must not be negative");
		}
		return IntStream.rangeClosed(1, quantity);
	}
}
