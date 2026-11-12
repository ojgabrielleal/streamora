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

	public ScheduleResponseDTO store(ScheduleRequestDTO scheduleRequest) {
		validateInterval(scheduleRequest);

		Playlist playlist = findPlaylistByUuid(scheduleRequest.playlistUuid());

		Schedule schedule = ScheduleMapper.toEntity(scheduleRequest, playlist);

		return ScheduleMapper.toResponse(scheduleRepository.save(schedule));
	}

	public ScheduleResponseDTO update(String uuid, ScheduleRequestDTO scheduleRequest) {
		validateInterval(scheduleRequest);

		Schedule storedSchedule = findScheduleByUuid(uuid);
		Playlist playlist = findPlaylistByUuid(scheduleRequest.playlistUuid());

		ScheduleMapper.toEntity(scheduleRequest, playlist, storedSchedule);

		return ScheduleMapper.toResponse(scheduleRepository.save(storedSchedule));
	}

	public void delete(String uuid) {
		scheduleRepository.delete(findScheduleByUuid(uuid));
	}

	private void validateInterval(ScheduleRequestDTO scheduleRequest) {
		if (scheduleRequest.startTime() == null || scheduleRequest.endTime() == null
			|| !scheduleRequest.endTime().isAfter(scheduleRequest.startTime())) {
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
