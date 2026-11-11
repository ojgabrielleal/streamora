package com.streamora.streamora.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Playlist retornada pela API")
public record PlaylistResponseDTO(
	@Schema(description = "Identificador público da playlist", example = "550e8400-e29b-41d4-a716-446655440000")
	String uuid,
	@Schema(description = "Nome da playlist", example = "Programação da manhã")
	String name,
	@Schema(description = "Identificadores dos arquivos vinculados à playlist")
	Set<String> fileUuids,
	@Schema(description = "Horários vinculados à playlist")
	Set<PlaylistScheduleSummaryDTO> schedules
) {}
