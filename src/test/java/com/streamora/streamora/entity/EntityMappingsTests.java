package com.streamora.streamora.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
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

	@Test
	void generatesUuidBeforePersistingMainEntities() throws ReflectiveOperationException {
		for (Class<?> entityClass : MAIN_ENTITIES.keySet()) {
			Object entity = entityClass.getConstructor().newInstance();
			Method callback = entityClass.getDeclaredMethod("generateUuid");

			assertThat(callback.isAnnotationPresent(PrePersist.class)).isTrue();
			assertThat(Modifier.isPrivate(callback.getModifiers())).isTrue();

			callback.setAccessible(true);
			callback.invoke(entity);

			String uuid = (String) entityClass.getDeclaredMethod("getUuid").invoke(entity);
			assertThat(uuid).hasSize(36);
			assertThat(UUID.fromString(uuid).toString()).isEqualTo(uuid);
		}
	}
}
