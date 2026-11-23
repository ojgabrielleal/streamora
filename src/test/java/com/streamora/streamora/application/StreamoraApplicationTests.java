package com.streamora.streamora.application;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.streamora.streamora.StreamoraApplication;
import com.streamora.streamora.repository.PlaylistRepository;
import com.streamora.streamora.repository.ScheduleRepository;
import com.streamora.streamora.repository.SettingRepository;
import com.streamora.streamora.repository.UserRepository;

@SpringBootTest(
	classes = StreamoraApplication.class,
	properties = {
		"spring.autoconfigure.exclude="
			+ "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
			+ "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration",
		"jwt.secret=context-test-secret-that-is-not-used-outside-tests"
	}
)
class StreamoraApplicationTests {

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private PlaylistRepository playlistRepository;

	@MockitoBean
	private ScheduleRepository scheduleRepository;

	@MockitoBean
	private SettingRepository settingRepository;

	@Test
	void contextLoads() {
	}
}
