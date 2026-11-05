package com.streamora.streamora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.repository.PlaylistRepository;

@Service
@Transactional
public class PlaylistService {

	private final PlaylistRepository playlistRepository;

	public PlaylistService(PlaylistRepository playlistRepository) {
		this.playlistRepository = playlistRepository;
	}

	@Transactional(readOnly = true)
	public List<Playlist> index() {
		return playlistRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Playlist show(String uuid) {
		return findByUuid(uuid);
	}

	public Playlist store(Playlist playlist) {
		return playlistRepository.save(playlist);
	}

	public Playlist update(String uuid, Playlist playlist) {
		Playlist storedPlaylist = findByUuid(uuid);
		storedPlaylist.setName(playlist.getName());

		return playlistRepository.save(storedPlaylist);
	}

	public void delete(String uuid) {
		playlistRepository.delete(findByUuid(uuid));
	}

	private Playlist findByUuid(String uuid) {
		return playlistRepository.findByUuid(uuid)
				.orElseThrow(() -> new ResourceNotFoundException("Playlist", uuid));
	}
}
