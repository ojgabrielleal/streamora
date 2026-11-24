# Changelog

## [0.1.0] - 2026-11-23

### Novidades
- **Telemetria em tempo real (`GET /services/telemetry`):** exibição da contagem de ouvintes (Icecast) e da faixa em reprodução (Liquidsoap) no dashboard.
- **Capa da faixa:** extração automática da arte embutida na tag ID3v2 do MP3 (via `mp3agic`), com capa padrão quando não houver imagem.
- **Locução ao vivo:** exibição do nome do locutor autenticado no `input.harbor` durante transmissões ao vivo, retornando ao modo `Auto DJ` ao desconectar.
- **Controle de reinício do Liquidsoap:** botão **Reiniciar** exibido com destaque apenas quando há alterações pendentes (persistido no `localStorage`).

### Correções
- Ajuste de permissão de leitura (`644`) nos arquivos `.m3u` gerados para o Liquidsoap.
- Limpeza de imports e variáveis não utilizadas em `UnixSocketLiquidsoapProcessControl`.
