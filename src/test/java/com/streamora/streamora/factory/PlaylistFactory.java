package com.streamora.streamora.factory;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.streamora.streamora.dto.PlaylistRequestDTO;
import com.streamora.streamora.dto.PlaylistResponseDTO;
import com.streamora.streamora.entity.Playlist;

public final class PlaylistFactory {

	private PlaylistFactory() {}

	public static PlaylistRequestDTO toCreate() {
		return toCreate(1).getFirst();
	}

	public static List<PlaylistRequestDTO> toCreate(int quantity) {
		return indexes(quantity)
			.mapToObj(index -> new PlaylistRequestDTO("Playlist " + index, "playlist-" + index + ".m3u"))
			.toList();
	}

	public static Playlist toEntity() {
		return toEntity(1).getFirst();
	}

	public static List<Playlist> toEntity(int quantity) {
		return indexes(quantity)
			.mapToObj(index -> {
				Playlist playlist = new Playlist();
				playlist.setId((long) index);
				playlist.setUuid("playlist-uuid-" + index);
				playlist.setName("Playlist " + index);
				playlist.setM3uFileName("playlist-" + index + ".m3u");
				return playlist;
			})
			.toList();
	}

	public static PlaylistResponseDTO toResponse() {
		return toResponse(1).getFirst();
	}

	public static List<PlaylistResponseDTO> toResponse(int quantity) {
		return indexes(quantity)
			.mapToObj(index -> new PlaylistResponseDTO(
				"playlist-uuid-" + index,
				"Playlist " + index,
				"playlist-" + index + ".m3u",
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
