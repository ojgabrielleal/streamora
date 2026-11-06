package com.streamora.streamora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamora.streamora.dto.PlaylistRequestDTO;
import com.streamora.streamora.dto.PlaylistResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.mapper.PlaylistMapper;
import com.streamora.streamora.repository.PlaylistRepository;

@Service
@Transactional
public class PlaylistService {

	private final PlaylistRepository playlistRepository;

	public PlaylistService(PlaylistRepository playlistRepository) {
		this.playlistRepository = playlistRepository;
	}

	@Transactional(readOnly = true)
	public List<PlaylistResponseDTO> index() {
		return playlistRepository.findAll().stream()
				.map(playlist -> PlaylistMapper.toResponse(playlist))
				.toList();
	}

	@Transactional(readOnly = true)
	public PlaylistResponseDTO show(String uuid) {
		return PlaylistMapper.toResponse(findByUuid(uuid));
	}

	public PlaylistResponseDTO store(PlaylistRequestDTO request) {
		Playlist playlist = PlaylistMapper.toEntity(request);

		return PlaylistMapper.toResponse(playlistRepository.save(playlist));
	}

	public PlaylistResponseDTO update(String uuid, PlaylistRequestDTO request) {
		Playlist storedPlaylist = findByUuid(uuid);
		PlaylistMapper.toEntity(request, storedPlaylist);

		return PlaylistMapper.toResponse(playlistRepository.save(storedPlaylist));
	}

	public void delete(String uuid) {
		playlistRepository.delete(findByUuid(uuid));
	}

	private Playlist findByUuid(String uuid) {
		return playlistRepository.findByUuid(uuid)
				.orElseThrow(() -> new ResourceNotFoundException("Playlist", uuid));
	}
}
