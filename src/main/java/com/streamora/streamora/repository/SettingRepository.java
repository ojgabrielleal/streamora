package com.streamora.streamora.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streamora.streamora.entity.Setting;

public interface SettingRepository extends JpaRepository<Setting, Long> {

	Optional<Setting> findBySettingKey(String settingKey);
}
