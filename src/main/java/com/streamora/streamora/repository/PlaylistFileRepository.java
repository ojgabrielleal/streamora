package com.streamora.streamora.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streamora.streamora.entity.PlaylistFile;

public interface PlaylistFileRepository extends JpaRepository<PlaylistFile, Long> {

	Optional<PlaylistFile> findByUuid(String uuid);
}
