package com.streamora.streamora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamora.streamora.dto.PlaylistScheduleRequestDTO;
import com.streamora.streamora.dto.PlaylistScheduleResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistSchedule;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.mapper.PlaylistScheduleMapper;
import com.streamora.streamora.repository.PlaylistRepository;
import com.streamora.streamora.repository.PlaylistScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaylistScheduleService {

	private final PlaylistScheduleRepository playlistScheduleRepository;
	private final PlaylistRepository playlistRepository;

	@Transactional(readOnly = true)
	public List<PlaylistScheduleResponseDTO> index() {
		return playlistScheduleRepository.findAll().stream()
			.map(PlaylistScheduleMapper::toResponse)
			.toList();
	}

	@Transactional(readOnly = true)
	public PlaylistScheduleResponseDTO show(String uuid) {
		return PlaylistScheduleMapper.toResponse(findScheduleByUuid(uuid));
	}

	public PlaylistScheduleResponseDTO store(PlaylistScheduleRequestDTO request) {
		validateInterval(request);

		Playlist playlist = findPlaylistByUuid(request.playlistUuid());

		PlaylistSchedule schedule = PlaylistScheduleMapper.toEntity(request, playlist);
		return PlaylistScheduleMapper.toResponse(playlistScheduleRepository.save(schedule));
	}

	public PlaylistScheduleResponseDTO update(String uuid, PlaylistScheduleRequestDTO request) {
		validateInterval(request);

		PlaylistSchedule storedSchedule = findScheduleByUuid(uuid);
		Playlist playlist = findPlaylistByUuid(request.playlistUuid());
		
		PlaylistScheduleMapper.toEntity(request, playlist, storedSchedule);
		return PlaylistScheduleMapper.toResponse(playlistScheduleRepository.save(storedSchedule));
	}

	public void delete(String uuid) {
		playlistScheduleRepository.delete(findScheduleByUuid(uuid));
	}

	private void validateInterval(PlaylistScheduleRequestDTO request) {
		if (request.startTime() == null || request.endTime() == null || !request.endTime().isAfter(request.startTime())) {
			throw new IllegalArgumentException("End time must be after start time");
		}
	}

	private PlaylistSchedule findScheduleByUuid(String uuid) {
		return playlistScheduleRepository.findByUuid(uuid)
			.orElseThrow(() -> new ResourceNotFoundException("PlaylistSchedule", uuid));
	}

	private Playlist findPlaylistByUuid(String uuid) {
		return playlistRepository.findByUuid(uuid)
			.orElseThrow(() -> new ResourceNotFoundException("Playlist", uuid));
	}
}
