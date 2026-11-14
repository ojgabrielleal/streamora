package com.streamora.streamora.factory;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.streamora.streamora.dto.playlists.PlaylistRequestDTO;
import com.streamora.streamora.dto.playlists.PlaylistResponseDTO;
import com.streamora.streamora.dto.users.UserRequestDTO;
import com.streamora.streamora.dto.users.UserResponseDTO;
import com.streamora.streamora.entity.Playlist;
import com.streamora.streamora.entity.User;

class FactoriesTests {

	@Test
	void createsSingleUserRequest() {
		assertThat(UserFactory.toCreate()).isInstanceOf(UserRequestDTO.class);
	}

	@Test
	void createsRequestedQuantityOfUserRequests() {
		assertThat(UserFactory.toCreate(2))
			.hasSize(2)
			.doesNotHaveDuplicates();
	}

	@Test
	void createsSingleUserEntity() {
		assertThat(UserFactory.toEntity()).isInstanceOf(User.class);
	}

	@Test
	void createsRequestedQuantityOfUserEntities() {
		assertThat(UserFactory.toEntity(2))
			.hasSize(2)
			.extracting(user -> user.getUuid())
			.doesNotHaveDuplicates();
	}

	@Test
	void createsSingleUserResponse() {
		assertThat(UserFactory.toResponse()).isInstanceOf(UserResponseDTO.class);
	}

	@Test
	void createsRequestedQuantityOfUserResponses() {
		assertThat(UserFactory.toResponse(2))
			.hasSize(2)
			.doesNotHaveDuplicates();
	}

	@Test
	void createsSinglePlaylistRequest() {
		assertThat(PlaylistFactory.toCreate()).isInstanceOf(PlaylistRequestDTO.class);
	}

	@Test
	void createsRequestedQuantityOfPlaylistRequests() {
		assertThat(PlaylistFactory.toCreate(2))
			.hasSize(2)
			.doesNotHaveDuplicates();
	}

	@Test
	void createsSinglePlaylistEntity() {
		assertThat(PlaylistFactory.toEntity()).isInstanceOf(Playlist.class);
	}

	@Test
	void createsRequestedQuantityOfPlaylistEntities() {
		assertThat(PlaylistFactory.toEntity(2))
			.hasSize(2)
			.extracting(playlist -> playlist.getUuid())
			.doesNotHaveDuplicates();
	}

	@Test
	void createsSinglePlaylistResponse() {
		assertThat(PlaylistFactory.toResponse()).isInstanceOf(PlaylistResponseDTO.class);
	}

	@Test
	void createsRequestedQuantityOfPlaylistResponses() {
		assertThat(PlaylistFactory.toResponse(2))
			.hasSize(2)
			.doesNotHaveDuplicates();
	}

}
