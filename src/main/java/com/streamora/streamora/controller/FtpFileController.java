package com.streamora.streamora.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.streamora.streamora.dto.ftp.FtpDirectoryCreateRequestDTO;
import com.streamora.streamora.dto.ftp.FtpFileEntryResponseDTO;
import com.streamora.streamora.dto.ftp.FtpMoveRequestDTO;
import com.streamora.streamora.dto.ftp.FtpRenameRequestDTO;
import com.streamora.streamora.dto.ftp.FtpStorageErrorDTO;
import com.streamora.streamora.infrastructure.pureFtpd.storage.exception.FtpFileStorageException;
import com.streamora.streamora.mapper.FtpFileEntryMapper;
import com.streamora.streamora.service.FtpFileService;

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
@RequestMapping("/storage")
@RequiredArgsConstructor
@Tag(name = "Storage FTP", description = "Consulta e manipulação do storage compartilhado com o Pure-FTPd")
public class FtpFileController {

	private final FtpFileService ftpFileService;

	@GetMapping
	@Operation(summary = "Listar árvore do storage FTP")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Árvore do storage encontrada",
			content = @Content(
				array = @ArraySchema(
					schema = @Schema(implementation = FtpFileEntryResponseDTO.class)
				)
			)
		),
		@ApiResponse(
			responseCode = "400",
			description = "Falha ao listar o storage",
			content = @Content(schema = @Schema(implementation = FtpStorageErrorDTO.class))
		),
		@ApiResponse(responseCode = "401", description = "Autenticação necessária")
	})
	public List<FtpFileEntryResponseDTO> list() {
		return ftpFileService.list().stream()
			.map(FtpFileEntryMapper::toResponse)
			.toList();
	}

	@DeleteMapping("/files")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Excluir arquivo")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Arquivo excluído"),
		@ApiResponse(
			responseCode = "400",
			description = "Falha ao excluir o arquivo",
			content = @Content(schema = @Schema(implementation = FtpStorageErrorDTO.class))
		),
		@ApiResponse(responseCode = "401", description = "Autenticação necessária")
	})
	public void deleteFile(
		@Parameter(description = "Caminho relativo do arquivo", example = "programas/abertura.mp3", required = true)
		@RequestParam String path
	)
	{
		ftpFileService.deleteFile(path);
	}

	@PutMapping("/files/rename")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Renomear arquivo")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Arquivo renomeado"),
		@ApiResponse(
			responseCode = "400",
			description = "Falha ao renomear o arquivo",
			content = @Content(schema = @Schema(implementation = FtpStorageErrorDTO.class))
		),
		@ApiResponse(responseCode = "401", description = "Autenticação necessária")
	})
	public void renameFile(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Arquivo e novo nome", required = true)
		@RequestBody FtpRenameRequestDTO ftpRenameRequest
	)
	{
		ftpFileService.renameFile(ftpRenameRequest.path(), ftpRenameRequest.newName());
	}

	@PutMapping("/files/move")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Mover arquivo")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Arquivo movido"),
		@ApiResponse(
			responseCode = "400",
			description = "Falha ao mover o arquivo",
			content = @Content(schema = @Schema(implementation = FtpStorageErrorDTO.class))
		),
		@ApiResponse(responseCode = "401", description = "Autenticação necessária")
	})
	public void moveFile(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Arquivo e diretório de destino", required = true)
		@RequestBody FtpMoveRequestDTO ftpMoveRequest
	)
	{
		ftpFileService.moveFile(ftpMoveRequest.path(), ftpMoveRequest.targetDirectory());
	}

	@PostMapping("/directories")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Criar diretório")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Diretório criado"),
		@ApiResponse(
			responseCode = "400",
			description = "Falha ao criar o diretório",
			content = @Content(schema = @Schema(implementation = FtpStorageErrorDTO.class))
		),
		@ApiResponse(responseCode = "401", description = "Autenticação necessária")
	})
	public void createDirectory(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Caminho do novo diretório", required = true)
		@RequestBody FtpDirectoryCreateRequestDTO ftpDirectoryCreateRequest
	)
	{
		ftpFileService.createDirectory(ftpDirectoryCreateRequest.path());
	}

	@PutMapping("/directories/rename")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Renomear diretório")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Diretório renomeado"),
		@ApiResponse(
			responseCode = "400",
			description = "Falha ao renomear o diretório",
			content = @Content(schema = @Schema(implementation = FtpStorageErrorDTO.class))
		),
		@ApiResponse(responseCode = "401", description = "Autenticação necessária")
	})
	public void renameDirectory(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Diretório e novo nome", required = true)
		@RequestBody FtpRenameRequestDTO ftpRenameRequest
	)
	{
		ftpFileService.renameDirectory(ftpRenameRequest.path(), ftpRenameRequest.newName());
	}

	@DeleteMapping("/directories")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Excluir diretório vazio")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Diretório excluído"),
		@ApiResponse(
			responseCode = "400",
			description = "Falha ao excluir o diretório",
			content = @Content(schema = @Schema(implementation = FtpStorageErrorDTO.class))
		),
		@ApiResponse(responseCode = "401", description = "Autenticação necessária")
	})
	public void deleteDirectory(
		@Parameter(description = "Caminho relativo do diretório", example = "programas/antigos", required = true)
		@RequestParam String path
	)
	{
		ftpFileService.deleteDirectory(path);
	}

	@ExceptionHandler(FtpFileStorageException.class)
	public ResponseEntity<FtpStorageErrorDTO> handleStorageFailure(FtpFileStorageException exception) {
		return ResponseEntity.badRequest()
			.body(new FtpStorageErrorDTO(exception.getMessage()));
	}
}
