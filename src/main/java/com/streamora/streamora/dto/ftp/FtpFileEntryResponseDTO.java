package com.streamora.streamora.dto.ftp;

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
