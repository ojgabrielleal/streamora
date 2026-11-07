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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/playlist-file")
@RequiredArgsConstructor
@Tag(name = "Arquivos de playlist", description = "Gerenciamento dos arquivos usados nas playlists")
public class PlaylistFileController {

	private final PlaylistFileService playlistFileService;

	@GetMapping
	@Operation(summary = "Listar arquivos de playlist")
	@ApiResponse(
		responseCode = "200",
		description = "Arquivos encontrados",
		content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlaylistFileResponseDTO.class)))
	)
	public List<PlaylistFileResponseDTO> index() {
		return playlistFileService.index();
	}

	@GetMapping("/{uuid}")
	@Operation(summary = "Consultar arquivo de playlist")
	@ApiResponse(responseCode = "200", description = "Arquivo encontrado")
	public PlaylistFileResponseDTO show(
		@Parameter(description = "UUID do arquivo", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable String uuid
	) {
		return playlistFileService.show(uuid);
	}

	@PostMapping
	@Operation(summary = "Criar arquivo de playlist")
	@ApiResponse(responseCode = "200", description = "Arquivo criado")
	public PlaylistFileResponseDTO store(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do arquivo", required = true)
		@RequestBody PlaylistFileRequestDTO playlistFileRequest
	) {
		return playlistFileService.store(playlistFileRequest);
	}

	@PutMapping("/{uuid}")
	@Operation(summary = "Atualizar arquivo de playlist")
	@ApiResponse(responseCode = "200", description = "Arquivo atualizado")
	public PlaylistFileResponseDTO update(
		@Parameter(description = "UUID do arquivo", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable String uuid,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados do arquivo", required = true)
		@RequestBody PlaylistFileRequestDTO playlistFileRequest
	) {
		return playlistFileService.update(uuid, playlistFileRequest);
	}

	@DeleteMapping("/{uuid}")
	@Operation(summary = "Excluir arquivo de playlist")
	@ApiResponse(responseCode = "200", description = "Arquivo excluído")
	public void delete(
		@Parameter(description = "UUID do arquivo", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable String uuid
	) {
		playlistFileService.delete(uuid);
	}

	@PostMapping("/{fileUuid}/playlist/{playlistUuid}")
	@Operation(summary = "Adicionar arquivo a uma playlist")
	@ApiResponse(responseCode = "200", description = "Arquivo vinculado à playlist")
	public PlaylistFileResponseDTO addToPlaylist(
		@Parameter(description = "UUID do arquivo", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable String fileUuid,
		@Parameter(description = "UUID da playlist", example = "9d13b7c1-0a2e-4f62-9430-1905c30ef340")
		@PathVariable String playlistUuid
	) {
		return playlistFileService.addToPlaylist(fileUuid, playlistUuid);
	}

	@DeleteMapping("/{fileUuid}/playlist/{playlistUuid}")
	@Operation(summary = "Remover arquivo de uma playlist")
	@ApiResponse(responseCode = "200", description = "Vínculo entre arquivo e playlist removido")
	public PlaylistFileResponseDTO removeFromPlaylist(
		@Parameter(description = "UUID do arquivo", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable String fileUuid,
		@Parameter(description = "UUID da playlist", example = "9d13b7c1-0a2e-4f62-9430-1905c30ef340")
		@PathVariable String playlistUuid
	) {
		return playlistFileService.removeFromPlaylist(fileUuid, playlistUuid);
	}
}
