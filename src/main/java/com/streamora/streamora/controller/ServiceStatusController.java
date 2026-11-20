package com.streamora.streamora.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.streamora.streamora.dto.services.ServiceStatusResponseDTO;
import com.streamora.streamora.service.RadioServiceStatusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Tag(name = "Services", description = "Monitoramento dos serviços da rádio")
public class ServiceStatusController {

	private final RadioServiceStatusService radioServiceStatusService;

	@GetMapping("/status")
	@Operation(summary = "Consultar disponibilidade do Icecast e do Liquidsoap")
	@ApiResponse(responseCode = "200", description = "Estados consultados")
	@ApiResponse(responseCode = "401", description = "Autenticação necessária")
	public ServiceStatusResponseDTO show() {
		return radioServiceStatusService.getStatus();
	}
}
