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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
@Tag(name = "Playlists", description = "Gerenciamento de playlists")
public class PlaylistController {

	private final PlaylistService playlistService;

	@GetMapping
	@Operation(summary = "Listar playlists")
	@ApiResponse(
		responseCode = "200",
		description = "Playlists encontradas",
		content = @Content(
			array = @ArraySchema(
				schema = @Schema(implementation = PlaylistResponseDTO.class)
			)
		)
	)
	public List<PlaylistResponseDTO> index() {
		return playlistService.index();
	}

	@GetMapping("/{uuid}")
	@Operation(summary = "Consultar playlist")
	@ApiResponse(responseCode = "200", description = "Playlist encontrada")
	public PlaylistResponseDTO show(
		@Parameter(description = "UUID da playlist", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable String uuid
	)
	{
		return playlistService.show(uuid);
	}

	@PostMapping
	@Operation(summary = "Criar playlist")
	@ApiResponse(responseCode = "200", description = "Playlist criada")
	public PlaylistResponseDTO store(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados da playlist", required = true)
		@RequestBody PlaylistRequestDTO playlistRequest
	)
	{
		return playlistService.store(playlistRequest);
	}

	@PutMapping("/{uuid}")
	@Operation(summary = "Atualizar playlist")
	@ApiResponse(responseCode = "200", description = "Playlist atualizada")
	public PlaylistResponseDTO update(
		@Parameter(description = "UUID da playlist", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable String uuid,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados da playlist", required = true)
		@RequestBody PlaylistRequestDTO playlistRequest
	)
	{
		return playlistService.update(uuid, playlistRequest);
	}

	@DeleteMapping("/{uuid}")
	@Operation(summary = "Excluir playlist")
	@ApiResponse(responseCode = "200", description = "Playlist excluída")
	public void delete(
		@Parameter(description = "UUID da playlist", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable String uuid
	)
	{
		playlistService.delete(uuid);
	}
}
