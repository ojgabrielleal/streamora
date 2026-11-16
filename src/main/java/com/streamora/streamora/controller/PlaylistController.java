package com.streamora.streamora.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.streamora.streamora.dto.playlists.PlaylistDetailsResponseDTO;
import com.streamora.streamora.dto.playlists.PlaylistErrorDTO;
import com.streamora.streamora.dto.playlists.PlaylistRequestDTO;
import com.streamora.streamora.dto.playlists.PlaylistResponseDTO;
import com.streamora.streamora.dto.playlists.PlaylistTracksRequestDTO;
import com.streamora.streamora.exception.ResourceNotFoundException;
import com.streamora.streamora.infrastructure.liquidsoap.exception.LiquidsoapFileException;
import com.streamora.streamora.infrastructure.liquidsoap.exception.M3uReadException;
import com.streamora.streamora.service.LiquidsoapService;
import com.streamora.streamora.service.PlaylistService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
@Tag(name = "Playlists", description = "Gerenciamento de playlists")
public class PlaylistController {

	private final PlaylistService playlistService;
	private final LiquidsoapService liquidsoapService;

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
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Playlist encontrada",
			content = @Content(schema = @Schema(implementation = PlaylistDetailsResponseDTO.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Playlist não encontrada",
			content = @Content(schema = @Schema(implementation = PlaylistErrorDTO.class))
		),
		@ApiResponse(
			responseCode = "409",
			description = "M3U associado indisponível ou inválido",
			content = @Content(schema = @Schema(implementation = PlaylistErrorDTO.class))
		),
		@ApiResponse(responseCode = "401", description = "Autenticação necessária")
	})
	public PlaylistDetailsResponseDTO show(
		@Parameter(description = "UUID da playlist", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable String uuid
	)
	{
		return liquidsoapService.showPlaylistDetails(uuid);
	}

	@PutMapping("/{uuid}/tracks")
	@Operation(
		summary = "Definir faixas da playlist",
		description = "Cria e associa o M3U quando a playlist ainda não possui faixas; caso contrário, substitui integralmente seu conteúdo."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Faixas definidas e M3U criado ou atualizado",
			content = @Content(schema = @Schema(implementation = PlaylistDetailsResponseDTO.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "Lista ou caminho de mídia inválido",
			content = @Content(schema = @Schema(implementation = PlaylistErrorDTO.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Playlist não encontrada",
			content = @Content(schema = @Schema(implementation = PlaylistErrorDTO.class))
		),
		@ApiResponse(responseCode = "401", description = "Autenticação necessária")
	})
	public PlaylistDetailsResponseDTO setTracks(
		@Parameter(description = "UUID da playlist", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable String uuid,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Estado completo desejado das faixas", required = true)
		@RequestBody PlaylistTracksRequestDTO playlistTracksRequest
	)
	{
		return liquidsoapService.setPlaylistTracks(uuid, playlistTracksRequest.paths());
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

	@ExceptionHandler(LiquidsoapFileException.class)
	public ResponseEntity<PlaylistErrorDTO> handleInvalidTracks(LiquidsoapFileException exception) {
		return ResponseEntity.badRequest()
			.body(new PlaylistErrorDTO(exception.getMessage()));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<PlaylistErrorDTO> handleNotFound(ResourceNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new PlaylistErrorDTO(exception.getMessage()));
	}

	@ExceptionHandler(M3uReadException.class)
	public ResponseEntity<PlaylistErrorDTO> handleM3uReadFailure(M3uReadException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new PlaylistErrorDTO(exception.getMessage()));
	}
}
