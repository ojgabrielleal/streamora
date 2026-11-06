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

import com.streamora.streamora.dto.PlaylistFileRequestDTO;
import com.streamora.streamora.dto.PlaylistFileResponseDTO;
import com.streamora.streamora.service.PlaylistFileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/playlist-file")
@RequiredArgsConstructor
public class PlaylistFileController {

	private final PlaylistFileService playlistFileService;

	@GetMapping
	public List<PlaylistFileResponseDTO> index() {
		return playlistFileService.index();
	}

	@GetMapping("/{uuid}")
	public PlaylistFileResponseDTO show(@PathVariable String uuid) {
		return playlistFileService.show(uuid);
	}

	@PostMapping
	public PlaylistFileResponseDTO store(@RequestBody PlaylistFileRequestDTO playlistFileRequest) {
		return playlistFileService.store(playlistFileRequest);
	}

	@PutMapping("/{uuid}")
	public PlaylistFileResponseDTO update(@PathVariable String uuid, @RequestBody PlaylistFileRequestDTO playlistFileRequest) {
		return playlistFileService.update(uuid, playlistFileRequest);
	}

	@DeleteMapping("/{uuid}")
	public void delete(@PathVariable String uuid) {
		playlistFileService.delete(uuid);
	}

	@PostMapping("/{fileUuid}/playlist/{playlistUuid}")
	public PlaylistFileResponseDTO addToPlaylist(@PathVariable String fileUuid, @PathVariable String playlistUuid) {
		return playlistFileService.addToPlaylist(fileUuid, playlistUuid);
	}

	@DeleteMapping("/{fileUuid}/playlist/{playlistUuid}")
	public PlaylistFileResponseDTO removeFromPlaylist(@PathVariable String fileUuid, @PathVariable String playlistUuid) {
		return playlistFileService.removeFromPlaylist(fileUuid, playlistUuid);
	}
}
