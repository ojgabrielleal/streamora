package com.streamora.streamora.dto;

import java.util.Set;

import com.streamora.streamora.entity.type.PlaylistFileType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Arquivo de playlist retornado pela API")
public record PlaylistFileResponseDTO(
	@Schema(description = "Identificador público do arquivo", example = "550e8400-e29b-41d4-a716-446655440000")
	String uuid,
	@Schema(description = "Tipo do arquivo", example = "music")
	PlaylistFileType type,
	@Schema(description = "Nome do arquivo", example = "Música de abertura")
	String name,
	@Schema(description = "Caminho do arquivo de áudio", example = "/audio/abertura.mp3")
	String path,
	@Schema(description = "Identificadores das playlists vinculadas ao arquivo")
	Set<String> playlistUuids
) {}
