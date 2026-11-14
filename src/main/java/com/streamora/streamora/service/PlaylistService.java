package com.streamora.streamora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamora.streamora.dto.playlists.PlaylistRequestDTO;
import com.streamora.streamora.dto.playlists.PlaylistResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.mapper.PlaylistMapper;
import com.streamora.streamora.repository.PlaylistRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaylistService {

	private final PlaylistRepository playlistRepository;

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

	public PlaylistResponseDTO store(PlaylistRequestDTO playlistRequest) {
		Playlist playlistToPersist = PlaylistMapper.toEntity(playlistRequest);

		return PlaylistMapper.toResponse(playlistRepository.save(playlistToPersist));
	}

	public PlaylistResponseDTO update(String uuid, PlaylistRequestDTO playlistRequest) {
		Playlist storedPlaylist = findByUuid(uuid);
		PlaylistMapper.toEntity(playlistRequest, storedPlaylist);

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
