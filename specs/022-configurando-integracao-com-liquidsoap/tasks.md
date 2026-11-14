# Tarefas — Spec 022

- [x] Criar a migration de `is_base` e `weight` e atualizar o schema documentado.
- [x] Mapear `isBase` e `weight` na entity e nos contratos de playlist.
- [x] Atualizar factories e testes existentes afetados pelos novos campos.
- [x] Configurar diretórios e validar caminhos dentro do storage compartilhado.
- [x] Gerar e escrever M3U a partir de mídia, lista e diretório.
- [x] Associar e regenerar o M3U de uma playlist pelo `PlaylistService`.
- [x] Selecionar playlists base e agendamentos ativos pelos services existentes.
- [x] Validar fontes, eliminar duplicatas e aplicar pesos positivos.
- [x] Gerar separadamente as fontes ponderadas e a grade semanal em `.liq`.
- [x] Implementar o `LiquidsoapService` coordenador, sem controller.
- [x] Ajustar `radio.liq` para carregar os arquivos gerados.
- [x] Cobrir fallbacks e falhas individuais de playlists por testes unitários.
- [x] Atualizar `docs/project-structure.md` e documentar a limitação de reload.
- [x] Executar testes, compilação e revisão dos critérios de aceite.
