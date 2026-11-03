package com.streamora.streamora;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class MigrationFilesTests {

	private static final String MIGRATIONS_PATH = "db/migrations/";
	private static final Pattern CREATE_TABLE = Pattern.compile(
			"(?i)\\bCREATE\\s+TABLE\\s+([a-z_]+)");
	private static final Map<String, String> MIGRATIONS = Map.of(
			"V1__create_users_table.sql", "users",
			"V2__create_playlists_table.sql", "playlists",
			"V3__create_files_table.sql", "files",
			"V4__create_playlist_files_table.sql", "playlist_files");

	@Test
	void keepsOneTablePerMigration() throws IOException {
		for (Map.Entry<String, String> migration : MIGRATIONS.entrySet()) {
			String sql = readMigration(migration.getKey());
			Matcher matcher = CREATE_TABLE.matcher(sql);

			assertThat(matcher.find()).isTrue();
			assertThat(matcher.group(1)).isEqualTo(migration.getValue());
			assertThat(matcher.find()).isFalse();
		}
	}

	@Test
	void makesEveryUuidUnique() throws IOException {
		for (String migration : MIGRATIONS.keySet()) {
			String sql = readMigration(migration);
			if (sql.matches("(?is).*\\buuid\\b.*")) {
				assertThat(sql).containsPattern("(?i)uuid\\s+CHAR\\(36\\)\\s+NOT\\s+NULL\\s+UNIQUE");
			}
		}
	}

	@Test
	void definesFileTypesRequiredByTheSpecification() throws IOException {
		assertThat(readMigration("V3__create_files_table.sql"))
				.contains("'music'", "'station_id'", "'sweeper'", "'jingle'", "'commercial'", "'bumper'");
	}

	@Test
	void linksPlaylistFilesWithACompositePrimaryKey() throws IOException {
		assertThat(readMigration("V4__create_playlist_files_table.sql"))
				.contains("PRIMARY KEY (playlist_id, file_id)")
				.contains("FOREIGN KEY (playlist_id) REFERENCES playlists (id)")
				.contains("FOREIGN KEY (file_id) REFERENCES files (id)");
	}

	private String readMigration(String fileName) throws IOException {
		try (InputStream input = getClass().getClassLoader()
				.getResourceAsStream(MIGRATIONS_PATH + fileName)) {
			assertThat(input).as("migration %s", fileName).isNotNull();
			return new String(input.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
