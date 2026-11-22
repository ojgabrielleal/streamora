package com.streamora.streamora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.streamora.streamora.entity.Setting;
import com.streamora.streamora.repository.SettingRepository;

@ExtendWith(MockitoExtension.class)
class SettingServiceTest {

	@Mock
	private SettingRepository settingRepository;

	@InjectMocks
	private SettingService settingService;

	@Test
	void isLiveBroadcastEnabledReturnsTrueWhenSettingDoesNotExist() {
		when(settingRepository.findBySettingKey(SettingService.LIVE_BROADCAST_ENABLED_KEY))
			.thenReturn(Optional.empty());

		boolean enabled = settingService.isLiveBroadcastEnabled();

		assertThat(enabled).isTrue();
	}

	@Test
	void isLiveBroadcastEnabledReturnsFalseWhenSettingIsFalse() {
		Setting setting = new Setting();
		setting.setSettingKey(SettingService.LIVE_BROADCAST_ENABLED_KEY);
		setting.setSettingValue("false");
		when(settingRepository.findBySettingKey(SettingService.LIVE_BROADCAST_ENABLED_KEY))
			.thenReturn(Optional.of(setting));

		boolean enabled = settingService.isLiveBroadcastEnabled();

		assertThat(enabled).isFalse();
	}

	@Test
	void updateLiveBroadcastEnabledSavesSetting() {
		Setting setting = new Setting();
		setting.setSettingKey(SettingService.LIVE_BROADCAST_ENABLED_KEY);
		setting.setSettingValue("true");
		when(settingRepository.findBySettingKey(SettingService.LIVE_BROADCAST_ENABLED_KEY))
			.thenReturn(Optional.of(setting));
		when(settingRepository.save(any(Setting.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		boolean enabled = settingService.updateLiveBroadcastEnabled(false);

		assertThat(enabled).isFalse();
		assertThat(setting.getSettingValue()).isEqualTo("false");
		verify(settingRepository).save(setting);
	}
}
