package com.streamora.streamora.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

	@Column(name = "m3u_file_name")
	private String m3uFileName;

	@OneToMany(mappedBy = "playlist")
	private Set<Schedule> schedules = new HashSet<>();

	@PrePersist
	private void generateUuid() {
		uuid = UUID.randomUUID().toString();
	}
}
