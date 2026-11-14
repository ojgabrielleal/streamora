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

import com.streamora.streamora.dto.schedules.ScheduleRequestDTO;
import com.streamora.streamora.dto.schedules.ScheduleResponseDTO;
import com.streamora.streamora.service.ScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
@Tag(name = "Grade de programação", description = "Gerenciamento da grade de programação de playlists")
public class ScheduleController {

	private final ScheduleService scheduleService;

	@GetMapping
	@Operation(summary = "Listar horários de playlists")
	@ApiResponse(
		responseCode = "200",
		description = "Horários encontrados",
		content = @Content(
			array = @ArraySchema(
				schema = @Schema(implementation = ScheduleResponseDTO.class)
			)
		)
	)
	public List<ScheduleResponseDTO> index() {
		return scheduleService.index();
	}

	@GetMapping("/{uuid}")
	@Operation(summary = "Consultar horário de playlist")
	@ApiResponse(responseCode = "200", description = "Horário encontrado")
	public ScheduleResponseDTO show(
		@Parameter(description = "UUID do horário") @PathVariable String uuid
	)
	{
		return scheduleService.show(uuid);
	}

	@PostMapping
	@Operation(summary = "Criar horário de playlist")
	@ApiResponse(responseCode = "200", description = "Horário criado")
	public ScheduleResponseDTO store(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do horário", required = true)
		@RequestBody ScheduleRequestDTO scheduleRequest
	)
	{
		return scheduleService.store(scheduleRequest);
	}

	@PutMapping("/{uuid}")
	@Operation(summary = "Atualizar horário de playlist")
	@ApiResponse(responseCode = "200", description = "Horário atualizado")
	public ScheduleResponseDTO update(
		@Parameter(description = "UUID do horário") @PathVariable String uuid,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do horário", required = true)
		@RequestBody ScheduleRequestDTO scheduleRequest
	)
	{
		return scheduleService.update(uuid, scheduleRequest);
	}

	@DeleteMapping("/{uuid}")
	@Operation(summary = "Excluir horário de playlist")
	@ApiResponse(responseCode = "200", description = "Horário excluído")
	public void delete(
		@Parameter(description = "UUID do horário") @PathVariable String uuid
	)
	{
		scheduleService.delete(uuid);
	}
}
