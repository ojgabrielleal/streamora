package com.streamora.streamora.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streamora.streamora.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUuid(String uuid);
}
