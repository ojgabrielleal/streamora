# Tarefas - Spec 019

- [x] Criar quatro migrations independentes para remover `playlist_files`,
  remover `files`, adicionar `playlists.m3u_file_name` e renomear
  `playlist_schedules` para `schedules`.
- [x] Remover integralmente os componentes e testes de `PlaylistFile`.
- [x] Adicionar `m3uFileName` à entity, DTOs, mapper e operações de playlist.
- [x] Validar que `m3uFileName` não contenha caminho de diretório.
- [x] Renomear todos os componentes e testes de `PlaylistSchedule` para
  `Schedule` e alterar a rota para `/schedules`.
- [x] Atualizar testes estruturais, de contexto, migrations e OpenAPI.
- [x] Atualizar `docs/database-schema.md` e `docs/project-structure.md`.
- [x] Executar testes, compilação e conferência dos critérios de aceite.
- [x] Solicitar revisão/correções antes de finalizar a spec.
- [x] Após autorização, atualizar o status da spec e criar o commit.
