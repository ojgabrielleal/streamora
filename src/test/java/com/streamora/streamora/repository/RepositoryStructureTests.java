package com.streamora.streamora.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;

import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.PlaylistFile;
import com.streamora.streamora.entity.PlaylistSchedule;
import com.streamora.streamora.entity.User;

class RepositoryStructureTests {

	private static final Map<Class<?>, Class<?>> REPOSITORIES = Map.of(
		UserRepository.class, User.class,
		PlaylistRepository.class, Playlist.class,
		PlaylistFileRepository.class, PlaylistFile.class,
		PlaylistScheduleRepository.class, PlaylistSchedule.class
	);

	@Test
	void repositoriesUseExpectedEntityAndExposeUuidLookup() throws NoSuchMethodException {
		for (Map.Entry<Class<?>, Class<?>> entry : REPOSITORIES.entrySet()) {
			Class<?> repositoryClass = entry.getKey();
			Class<?> entityClass = entry.getValue();

			ResolvableType repositoryType = ResolvableType.forClass(repositoryClass).as(JpaRepository.class);
			Method findByUuid = repositoryClass.getMethod("findByUuid", String.class);

			assertThat(repositoryClass.isInterface()).isTrue();
			assertThat(repositoryType.resolveGeneric(0)).isEqualTo(entityClass);
			assertThat(repositoryType.resolveGeneric(1)).isEqualTo(Long.class);

			assertThat(ResolvableType.forMethodReturnType(findByUuid).resolve()).isEqualTo(Optional.class);
			assertThat(ResolvableType.forMethodReturnType(findByUuid).resolveGeneric(0)).isEqualTo(entityClass);
		}
	}

	@Test
	void userRepositoryExposesUsernameLookup() throws NoSuchMethodException {
		Method findByUsername = UserRepository.class.getMethod("findByUsername", String.class);

		assertThat(ResolvableType.forMethodReturnType(findByUsername).resolve()).isEqualTo(Optional.class);
		assertThat(ResolvableType.forMethodReturnType(findByUsername).resolveGeneric(0)).isEqualTo(User.class);
	}
}
