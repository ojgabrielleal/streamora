package com.streamora.streamora.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streamora.streamora.entity.PlaylistSchedule;

public interface PlaylistScheduleRepository extends JpaRepository<PlaylistSchedule, Long> {

	Optional<PlaylistSchedule> findByUuid(String uuid);
}
