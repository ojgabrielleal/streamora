package com.streamora.streamora.dto.ftp;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados públicos para conexão ao FTP")
public record FtpConnectionResponseDTO(
	@Schema(description = "Host público do FTP", example = "ftp.streamora.example")
	String host,
	@Schema(description = "Porta pública do FTP", example = "21")
	int port,
	@Schema(description = "Usuário FTP, igual ao acesso do painel", example = "joao.silva")
	String username
) {}
