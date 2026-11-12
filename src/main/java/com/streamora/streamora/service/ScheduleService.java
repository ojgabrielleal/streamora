package com.streamora.streamora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamora.streamora.dto.ScheduleRequestDTO;
import com.streamora.streamora.dto.ScheduleResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.Schedule;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.mapper.ScheduleMapper;
import com.streamora.streamora.repository.PlaylistRepository;
import com.streamora.streamora.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final PlaylistRepository playlistRepository;

	@Transactional(readOnly = true)
	public List<ScheduleResponseDTO> index() {
		return scheduleRepository.findAll().stream()
			.map(ScheduleMapper::toResponse)
			.toList();
	}

	@Transactional(readOnly = true)
	public ScheduleResponseDTO show(String uuid) {
		return ScheduleMapper.toResponse(findScheduleByUuid(uuid));
	}

	public ScheduleResponseDTO store(ScheduleRequestDTO request) {
		validateInterval(request);

		Playlist playlist = findPlaylistByUuid(request.playlistUuid());

		Schedule schedule = ScheduleMapper.toEntity(request, playlist);
		return ScheduleMapper.toResponse(scheduleRepository.save(schedule));
	}

	public ScheduleResponseDTO update(String uuid, ScheduleRequestDTO request) {
		validateInterval(request);

		Schedule storedSchedule = findScheduleByUuid(uuid);
		Playlist playlist = findPlaylistByUuid(request.playlistUuid());

		ScheduleMapper.toEntity(request, playlist, storedSchedule);
		return ScheduleMapper.toResponse(scheduleRepository.save(storedSchedule));
	}

	public void delete(String uuid) {
		scheduleRepository.delete(findScheduleByUuid(uuid));
	}

	private void validateInterval(ScheduleRequestDTO request) {
		if (request.startTime() == null || request.endTime() == null || !request.endTime().isAfter(request.startTime())) {
			throw new IllegalArgumentException("End time must be after start time");
		}
	}

	private Schedule findScheduleByUuid(String uuid) {
		return scheduleRepository.findByUuid(uuid)
			.orElseThrow(() -> new ResourceNotFoundException("Schedule", uuid));
	}

	private Playlist findPlaylistByUuid(String uuid) {
		return playlistRepository.findByUuid(uuid)
			.orElseThrow(() -> new ResourceNotFoundException("Playlist", uuid));
	}
}
