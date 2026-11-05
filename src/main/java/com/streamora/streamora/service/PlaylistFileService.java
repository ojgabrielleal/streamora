package com.streamora.streamora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistFile;
import com.streamora.streamora.repository.PlaylistFileRepository;
import com.streamora.streamora.repository.PlaylistRepository;

@Service
@Transactional
public class PlaylistFileService {

	private final PlaylistFileRepository playlistFileRepository;
	private final PlaylistRepository playlistRepository;

	public PlaylistFileService(
			PlaylistFileRepository playlistFileRepository,
			PlaylistRepository playlistRepository
	) {
		this.playlistFileRepository = playlistFileRepository;
		this.playlistRepository = playlistRepository;
	}

	@Transactional(readOnly = true)
	public List<PlaylistFile> index() {
		return playlistFileRepository.findAll();
	}

	@Transactional(readOnly = true)
	public PlaylistFile show(String uuid) {
		return findFileByUuid(uuid);
	}

	public PlaylistFile store(PlaylistFile playlistFile) {
		return playlistFileRepository.save(playlistFile);
	}

	public PlaylistFile update(String uuid, PlaylistFile playlistFile) {
		PlaylistFile storedFile = findFileByUuid(uuid);
		storedFile.setType(playlistFile.getType());
		storedFile.setName(playlistFile.getName());
		storedFile.setPath(playlistFile.getPath());

		return playlistFileRepository.save(storedFile);
	}

	public void delete(String uuid) {
		playlistFileRepository.delete(findFileByUuid(uuid));
	}

	public PlaylistFile addToPlaylist(String fileUuid, String playlistUuid) {
		PlaylistFile playlistFile = findFileByUuid(fileUuid);
		Playlist playlist = findPlaylistByUuid(playlistUuid);

		playlist.getFiles().add(playlistFile);
		playlistFile.getPlaylists().add(playlist);
		playlistRepository.save(playlist);

		return playlistFile;
	}

	public PlaylistFile removeFromPlaylist(String fileUuid, String playlistUuid) {
		PlaylistFile playlistFile = findFileByUuid(fileUuid);
		Playlist playlist = findPlaylistByUuid(playlistUuid);

		playlist.getFiles().remove(playlistFile);
		playlistFile.getPlaylists().remove(playlist);
		playlistRepository.save(playlist);

		return playlistFile;
	}

	private PlaylistFile findFileByUuid(String uuid) {
		return playlistFileRepository.findByUuid(uuid)
				.orElseThrow(() -> new ResourceNotFoundException("PlaylistFile", uuid));
	}

	private Playlist findPlaylistByUuid(String uuid) {
		return playlistRepository.findByUuid(uuid)
				.orElseThrow(() -> new ResourceNotFoundException("Playlist", uuid));
	}
}
