package com.streamora.streamora.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.streamora.streamora.dto.PlaylistRequestDTO;
import com.streamora.streamora.dto.PlaylistResponseDTO;
import com.streamora.streamora.service.PlaylistService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistController {

	private final PlaylistService playlistService;

	@GetMapping
	public List<PlaylistResponseDTO> index() {
		return playlistService.index();
	}

	@GetMapping("/{uuid}")
	public PlaylistResponseDTO show(@PathVariable String uuid) {
		return playlistService.show(uuid);
	}

	@PostMapping
	public PlaylistResponseDTO store(@RequestBody PlaylistRequestDTO playlistRequest) {
		return playlistService.store(playlistRequest);
	}

	@PutMapping("/{uuid}")
	public PlaylistResponseDTO update(@PathVariable String uuid, @RequestBody PlaylistRequestDTO playlistRequest) {
		return playlistService.update(uuid, playlistRequest);
	}

	@DeleteMapping("/{uuid}")
	public void delete(@PathVariable String uuid) {
		playlistService.delete(uuid);
	}
}
