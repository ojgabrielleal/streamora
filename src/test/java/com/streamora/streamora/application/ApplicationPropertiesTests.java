package com.streamora.streamora.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ApplicationPropertiesTests {

	private static final Properties PROPERTIES = new Properties();

	@BeforeAll
	static void loadApplicationProperties() throws IOException {
		try (InputStream input = ApplicationPropertiesTests.class.getClassLoader()
			.getResourceAsStream("application.properties")) {
			assertThat(input).isNotNull();
			PROPERTIES.load(input);
		}
	}

	@Test
	void configuresMySqlDatasourceWithEnvironmentVariables() {
		assertThat(PROPERTIES)
			.containsEntry(
				"spring.datasource.url",
				"jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DATABASE}"
			)
			.containsEntry("spring.datasource.username", "${MYSQL_USER}")
			.containsEntry("spring.datasource.password", "${MYSQL_PASSWORD}")
			.containsEntry("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
	}

	@Test
	void enablesFlywayWithItsDefaultMigrationLocation() {
		assertThat(PROPERTIES)
			.containsEntry("spring.flyway.enabled", "true")
			.doesNotContainKey("spring.flyway.locations");
	}

	@Test
	void configuresLiquidsoapSharedDirectories() {
		assertThat(PROPERTIES)
			.containsEntry(
				"liquidsoap.media-directory",
				"${LIQUIDSOAP_MEDIA_DIRECTORY:/pure-ftpd/uploads}"
			)
			.containsEntry(
				"liquidsoap.m3u-directory",
				"${LIQUIDSOAP_M3U_DIRECTORY:/liquidsoap/m3u}"
			)
			.containsEntry(
				"liquidsoap.config-directory",
				"${LIQUIDSOAP_CONFIG_DIRECTORY:/liquidsoap/config}"
			);
	}
}
