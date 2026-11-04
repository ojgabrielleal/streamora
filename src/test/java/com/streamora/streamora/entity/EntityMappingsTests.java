package com.streamora.streamora.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

class EntityMappingsTests {

	private static final Map<Class<?>, String> MAIN_ENTITIES = Map.of(
			User.class, "users",
			Playlist.class, "playlists",
			PlaylistFile.class, "files"
	);

	@Test
	void mapsMainEntitiesWithIdAndUuid() throws NoSuchFieldException {
		for (Map.Entry<Class<?>, String> entity : MAIN_ENTITIES.entrySet()) {
			Class<?> entityClass = entity.getKey();

			assertThat(entityClass.isAnnotationPresent(Entity.class)).isTrue();
			assertThat(entityClass.getAnnotation(Table.class).name()).isEqualTo(entity.getValue());
			assertThat(entityClass.getDeclaredField("id")).isNotNull();
			assertThat(entityClass.getDeclaredField("uuid")).isNotNull();
		}
	}
}
