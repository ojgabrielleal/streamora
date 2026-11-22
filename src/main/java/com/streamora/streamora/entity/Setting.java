package com.streamora.streamora.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
public class Setting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 36)
	private String uuid;

	@Column(name = "setting_key", nullable = false, unique = true)
	private String settingKey;

	@Column(name = "setting_value", nullable = false)
	private String settingValue;

	@PrePersist
	private void generateUuid() {
		uuid = UUID.randomUUID().toString();
	}
}
