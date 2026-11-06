package com.streamora.streamora.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "playlists")
@Getter
@Setter
@NoArgsConstructor
public class Playlist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 36)
	private String uuid;

	@Column(nullable = false)
	private String name;

	@ManyToMany
	@JoinTable(
		name = "playlist_files",
		joinColumns = @JoinColumn(name = "playlist_id"),
		inverseJoinColumns = @JoinColumn(name = "file_id")
	)
	private Set<PlaylistFile> files = new HashSet<>();

	@PrePersist
	private void generateUuid() {
		uuid = UUID.randomUUID().toString();
	}
}
