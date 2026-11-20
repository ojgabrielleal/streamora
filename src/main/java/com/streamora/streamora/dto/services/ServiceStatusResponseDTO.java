package com.streamora.streamora.dto.services;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Disponibilidade dos serviços responsáveis pela rádio")
public record ServiceStatusResponseDTO(
	@Schema(example = "ONLINE")
	ServiceAvailabilityStatus icecast,
	@Schema(example = "OFFLINE")
	ServiceAvailabilityStatus liquidsoap
) {}
