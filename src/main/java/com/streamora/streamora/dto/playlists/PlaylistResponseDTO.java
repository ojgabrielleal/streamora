package com.streamora.streamora.dto.playlists;

import java.util.Set;

import com.streamora.streamora.dto.schedules.ScheduleSummaryDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Playlist retornada pela API")
public record PlaylistResponseDTO(
	@Schema(description = "Identificador público da playlist", example = "550e8400-e29b-41d4-a716-446655440000")
	String uuid,
	@Schema(description = "Nome da playlist", example = "Programação da manhã")
	String name,
	@Schema(description = "Nome do arquivo M3U associado, sem caminho", example = "programacao-manha.m3u", nullable = true)
	String m3uFileName,
	@Schema(description = "Indica se a playlist participa do fallback base", example = "true")
	boolean isBase,
	@Schema(description = "Peso positivo da playlist nas combinações", example = "2", minimum = "1")
	int weight,
	@Schema(description = "Horários vinculados à playlist")
	Set<ScheduleSummaryDTO> schedules
) {}
