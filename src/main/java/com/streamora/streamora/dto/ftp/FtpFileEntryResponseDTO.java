package com.streamora.streamora.dto.ftp;

import java.time.Instant;
import java.util.List;

import com.streamora.streamora.infrastructure.pureFtpd.storage.records.FtpFileType;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Arquivo ou diretório presente no storage FTP")
public record FtpFileEntryResponseDTO(
	@Schema(description = "Nome do item", example = "programas")
	String name,
	@Schema(
		description = "Tipo do item",
		example = "DIRECTORY",
		allowableValues = {"DIRECTORY", "FILE"}
	)
	FtpFileType type,
	@Schema(description = "Extensão do arquivo sem o ponto", example = "mp3", nullable = true)
	String format,
	@Schema(description = "Tamanho do arquivo em bytes", example = "5242880", nullable = true)
	Long size,
	@Schema(description = "Data e hora da última alteração", example = "2026-11-20T14:30:00Z")
	Instant lastModified,
	@ArraySchema(
		arraySchema = @Schema(description = "Itens contidos no diretório"),
		schema = @Schema(implementation = FtpFileEntryResponseDTO.class)
	)
	List<FtpFileEntryResponseDTO> children
) {

	public FtpFileEntryResponseDTO {
		children = List.copyOf(children);
	}
}
