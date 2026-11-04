package com.streamora.streamora.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
public class PlaylistFile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 36)
	private String uuid;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PlaylistFileType type;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String path;

	@ManyToMany(mappedBy = "files")
	private Set<Playlist> playlists = new HashSet<>();
}
