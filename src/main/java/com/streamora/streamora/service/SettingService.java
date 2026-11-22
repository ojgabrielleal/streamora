package com.streamora.streamora.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamora.streamora.entity.Setting;
import com.streamora.streamora.repository.SettingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettingService {

	public static final String LIVE_BROADCAST_ENABLED_KEY = "live_broadcast_enabled";

	private final SettingRepository settingRepository;

	public boolean isLiveBroadcastEnabled() {
		return settingRepository.findBySettingKey(LIVE_BROADCAST_ENABLED_KEY)
			.map(setting -> Boolean.parseBoolean(setting.getSettingValue()))
			.orElse(true);
	}

	@Transactional
	public boolean updateLiveBroadcastEnabled(boolean enabled) {
		Setting setting = settingRepository.findBySettingKey(LIVE_BROADCAST_ENABLED_KEY)
			.orElseGet(() -> createSetting(LIVE_BROADCAST_ENABLED_KEY));

		setting.setSettingValue(Boolean.toString(enabled));
		settingRepository.save(setting);

		return enabled;
	}

	private Setting createSetting(String key) {
		Setting setting = new Setting();
		setting.setSettingKey(key);
		return setting;
	}
}
