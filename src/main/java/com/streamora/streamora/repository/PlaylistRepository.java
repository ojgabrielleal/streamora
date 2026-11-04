package com.streamora.streamora.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streamora.streamora.entity.Playlist;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

	Optional<Playlist> findByUuid(String uuid);
}
