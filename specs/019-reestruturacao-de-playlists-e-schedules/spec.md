# Spec 019 - Reestruturação de playlists e schedules

**Status:** Concluída

## Objetivo

Simplificar o gerenciamento de playlists, substituindo a estrutura de arquivos
individuais por uma referência ao arquivo M3U de cada playlist, e padronizar a
nomenclatura dos agendamentos de `playlist schedules` para `schedules`.

## Requisitos

- Remover as tabelas `playlist_files` e `files`.
- Remover integralmente os componentes de aplicação de `PlaylistFile`,
  incluindo entity, repository, service, controller, DTOs, mapper, enum e
  testes exclusivos.
- Adicionar à tabela `playlists` a coluna `m3u_file_name`, do tipo `VARCHAR`.
- Mapear a coluna `m3u_file_name` como `m3uFileName` na entity e nos contratos
  da aplicação.
- Armazenar em `m3u_file_name` somente o nome do arquivo M3U associado à
  playlist, sem caminho de diretório.
- Permitir que `m3u_file_name` permaneça vazio enquanto a geração dos arquivos
  M3U não estiver implementada.
- Ajustar os DTOs e mappers de playlist para incluir `m3uFileName`.
- Ajustar as operações de criação, atualização e consulta de playlists para o
  novo contrato.
- Remover todos os endpoints e operações de arquivos persistidos, incluindo a
  adição e remoção individual de arquivos de uma playlist.
- Renomear a tabela `playlist_schedules` para `schedules`, preservando os dados
  existentes.
- Renomear classes e componentes de `PlaylistSchedule` para `Schedule`,
  incluindo entity, repository, service, DTOs, mapper e controller.
- Alterar as rotas da API de playlist schedules para schedules.
- Ajustar todos os testes afetados pelas remoções e renomeações.
- Atualizar a documentação Swagger/OpenAPI com o campo `m3uFileName`, os novos
  contratos e as rotas de schedules.
- Remover da documentação Swagger/OpenAPI os endpoints descontinuados.
- Atualizar `docs/database-schema.md` com as alterações de banco de dados.
- Atualizar `docs/project-structure.md` para refletir os componentes removidos
  e renomeados.

## Regras

- Cada registro de playlist deve referenciar no máximo um arquivo M3U por meio
  de `m3u_file_name`.
- O valor de `m3u_file_name` não deve conter o caminho completo do arquivo.
- A geração ou alteração física dos arquivos M3U não faz parte desta spec.
- A configuração do Liquidsoap não deve ser alterada nesta spec.
- A associação simultânea de playlists de músicas e vinhetas a um schedule,
  seus pesos e a composição com `rotate` ficam fora do escopo desta spec.
- O comportamento de playlist padrão ou fallback do Liquidsoap fica fora do
  escopo desta spec.
- Cada migration deve modificar somente uma tabela, conforme as convenções do
  projeto.
- A remoção de `playlist_files`, a remoção de `files`, a adição de
  `playlists.m3u_file_name` e a renomeação de `playlist_schedules` devem ser
  feitas em migrations separadas.
- A tabela `playlist_files` deve ser removida antes da tabela `files`, para
  respeitar a chave estrangeira existente.
- A renomeação de `playlist_schedules` deve preservar os registros existentes,
  sem recriar ou descartar os dados da tabela.
- Não devem permanecer referências de código ou documentação aos componentes e
  endpoints removidos.

## Critérios de aceite

- [ ] A tabela `playlist_files` não existe após a execução das migrations.
- [ ] A tabela `files` não existe após a execução das migrations.
- [ ] A tabela `playlists` possui a coluna opcional `m3u_file_name` do tipo
  `VARCHAR`.
- [ ] A API de playlists recebe e devolve `m3uFileName` nos contratos
  aplicáveis.
- [ ] Criação, atualização e consulta de playlists funcionam sem depender de
  `playlist_files`.
- [ ] Os componentes, contratos, métodos e endpoints de `PlaylistFile` não
  existem mais.
- [ ] A tabela `schedules` substitui `playlist_schedules` sem perda dos dados
  existentes.
- [ ] Classes, componentes e rotas utilizam a nomenclatura `Schedule` e
  `schedules`.
- [ ] A documentação Swagger/OpenAPI representa os contratos e endpoints
  atualizados.
- [ ] `docs/database-schema.md` e `docs/project-structure.md` refletem a nova
  estrutura.
- [ ] As migrations respeitam a regra de uma tabela alterada por arquivo.
- [ ] O projeto compila e todos os testes passam.
