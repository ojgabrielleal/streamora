package com.streamora.streamora.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamora.streamora.dto.PlaylistFileRequestDTO;
import com.streamora.streamora.dto.PlaylistFileResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistFile;
import com.streamora.streamora.mapper.PlaylistFileMapper;
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
	public List<PlaylistFileResponseDTO> index() {
		return playlistFileRepository.findAll().stream()
				.map(playlistFile -> PlaylistFileMapper.toResponse(playlistFile))
				.toList();
	}

	@Transactional(readOnly = true)
	public PlaylistFileResponseDTO show(String uuid) {
		return PlaylistFileMapper.toResponse(findFileByUuid(uuid));
	}

	public PlaylistFileResponseDTO store(PlaylistFileRequestDTO request) {
		PlaylistFile playlistFile = PlaylistFileMapper.toEntity(request);
		playlistFile.setUuid(UUID.randomUUID().toString());

		return PlaylistFileMapper.toResponse(playlistFileRepository.save(playlistFile));
	}

	public PlaylistFileResponseDTO update(String uuid, PlaylistFileRequestDTO request) {
		PlaylistFile storedFile = findFileByUuid(uuid);
		PlaylistFile changes = PlaylistFileMapper.toEntity(request);
		storedFile.setType(changes.getType());
		storedFile.setName(changes.getName());
		storedFile.setPath(changes.getPath());

		return PlaylistFileMapper.toResponse(playlistFileRepository.save(storedFile));
	}

	public void delete(String uuid) {
		playlistFileRepository.delete(findFileByUuid(uuid));
	}

	public PlaylistFileResponseDTO addToPlaylist(String fileUuid, String playlistUuid) {
		PlaylistFile playlistFile = findFileByUuid(fileUuid);
		Playlist playlist = findPlaylistByUuid(playlistUuid);

		playlist.getFiles().add(playlistFile);
		playlistFile.getPlaylists().add(playlist);
		playlistRepository.save(playlist);

		return PlaylistFileMapper.toResponse(playlistFile);
	}

	public PlaylistFileResponseDTO removeFromPlaylist(String fileUuid, String playlistUuid) {
		PlaylistFile playlistFile = findFileByUuid(fileUuid);
		Playlist playlist = findPlaylistByUuid(playlistUuid);

		playlist.getFiles().remove(playlistFile);
		playlistFile.getPlaylists().remove(playlist);
		playlistRepository.save(playlist);

		return PlaylistFileMapper.toResponse(playlistFile);
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
