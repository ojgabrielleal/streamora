package com.streamora.streamora.application;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.streamora.streamora.StreamoraApplication;

@SpringBootTest(
		classes = StreamoraApplication.class,
		properties = {
			"spring.autoconfigure.exclude="
					+ "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
					+ "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration"
		})
class StreamoraApplicationTests {

	@Test
	void contextLoads() {
	}
}
