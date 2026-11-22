package com.streamora.streamora.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.streamora.streamora.dto.setting.LiveBroadcastStatusDTO;
import com.streamora.streamora.dto.setting.UpdateLiveBroadcastRequestDTO;
import com.streamora.streamora.service.SettingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@Tag(name = "Settings", description = "Configurações globais da rádio")
public class SettingController {

	private final SettingService settingService;

	@GetMapping("/live-broadcast")
	@Operation(summary = "Consultar status da locução ao vivo")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Status retornado"),
		@ApiResponse(responseCode = "401", description = "Autenticação necessária")
	})
	public LiveBroadcastStatusDTO getLiveBroadcastStatus() {
		return new LiveBroadcastStatusDTO(settingService.isLiveBroadcastEnabled());
	}

	@PutMapping("/live-broadcast")
	@Operation(summary = "Habilitar ou desabilitar a locução ao vivo")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Status atualizado"),
		@ApiResponse(responseCode = "401", description = "Autenticação necessária")
	})
	public LiveBroadcastStatusDTO updateLiveBroadcastStatus(
		@Valid @RequestBody UpdateLiveBroadcastRequestDTO request
	) {
		boolean enabled = settingService.updateLiveBroadcastEnabled(request.enabled());

		return new LiveBroadcastStatusDTO(enabled);
	}
}
