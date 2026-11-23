package com.streamora.streamora.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streamora.streamora.entity.User;
import com.streamora.streamora.entity.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUuid(String uuid);

	Optional<User> findByUsername(String username);

	long countByRole(UserRole role);
}
