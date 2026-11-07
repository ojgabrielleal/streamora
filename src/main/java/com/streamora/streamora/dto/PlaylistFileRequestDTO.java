package com.streamora.streamora.dto;

import com.streamora.streamora.entity.type.PlaylistFileType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criar ou atualizar um arquivo de playlist")
public record PlaylistFileRequestDTO(
	@Schema(description = "Tipo do arquivo", example = "music")
	PlaylistFileType type,
	@Schema(description = "Nome do arquivo", example = "Música de abertura")
	String name,
	@Schema(description = "Caminho do arquivo de áudio", example = "/audio/abertura.mp3")
	String path
) {}
