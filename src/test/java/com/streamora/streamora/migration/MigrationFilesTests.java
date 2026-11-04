package com.streamora.streamora.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

class MigrationFilesTests {

	private static final String MIGRATIONS_PATH = "db/migration/";
	private static final List<String> MAIN_TABLE_MIGRATIONS = List.of(
		"V1__create_users_table.sql",
		"V2__create_playlists_table.sql",
		"V3__create_files_table.sql"
	);

	@Test
	void mainTableMigrationsHaveIdAndUuidFields() throws IOException {
		for (String migration : MAIN_TABLE_MIGRATIONS) {
			assertThat(readMigration(migration))
				.as("main table migration %s", migration)
				.containsPattern("(?i)\\bid\\b")
				.containsPattern("(?i)\\buuid\\b");
		}
	}

	private String readMigration(String fileName) throws IOException {
		try (InputStream input = getClass().getClassLoader()
			.getResourceAsStream(MIGRATIONS_PATH + fileName)) {
				assertThat(input).as("migration %s", fileName).isNotNull();
				return new String(input.readAllBytes(), StandardCharsets.UTF_8);
			}
		}
}
