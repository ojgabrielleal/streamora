# Spec 034 - Criando tela de programação

**Status:** Concluída

## Objetivo

Disponibilizar a tela de **Programação** no front-end para visualizar, criar, editar e excluir horários da grade de programação de playlists, consumindo exclusivamente os endpoints já existentes no back-end (`/schedules` e `/playlists`).

## Contexto

O back-end já possui a API completa de grade de programação em `ScheduleController` (`/schedules`), permitindo listar, consultar, criar, atualizar e excluir horários vinculados a uma playlist, dia da semana (`WeekDay`) e intervalo (`startTime` e `endTime`). No front-end, já existem as telas de Dashboard (`/dashboard`), Arquivos (`/arquivos`) e Playlists (`/playlists`), porém ainda não há rota, cliente de API, queries/mutations nem interface para gerenciar a programação.

## Requisitos

### Rota e navegação

- Adicionar a rota protegida `/programacao` em `router.tsx` exigindo `accessToken` (`requireAccessToken`).
- Atualizar `AuthenticatedLayout` e `DashboardSidebar` para incluir o destino `'schedules'` (ou `'programacao'`) apontando para `/programacao` com o rótulo "Programação".

### Camada de API e TanStack Query

- Criar `schedule-types.ts`, `schedule-api.ts` e `schedule-queries.ts` em `front-end/src/api/schedules/` seguindo o padrão de `front-end/src/api/playlists/`.
- Integrar com os endpoints existentes do `ScheduleController`:
  - `GET /schedules` -> `ScheduleResponseDTO[]` (`uuid`, `playlist` (`PlaylistSummaryDTO`), `dayOfWeek`, `startTime`, `endTime`).
  - `POST /schedules` -> cria horário enviando `ScheduleRequestDTO` (`playlistUuid`, `dayOfWeek`, `startTime`, `endTime`).
  - `PUT /schedules/{uuid}` -> atualiza horário enviando `ScheduleRequestDTO`.
  - `DELETE /schedules/{uuid}` -> exclui horário.
- Reutilizar `playlistsQueryOptions()` para popular a seleção de playlist no formulário de criação/edição.
- Invalidar a query de schedules após criação, edição e exclusão.

### Interface da tela de Programação

- Exibir estado de carregamento, estado de erro com ação de tentar novamente e estado vazio quando não houver horários cadastrados.
- Exibir a grade de programação organizada por dia da semana (`MONDAY` a `SUNDAY`, com rótulos em português) e ordenada por `startTime`, mostrando para cada horário:
  - Nome da playlist associada;
  - Horário de início e término (`startTime` - `endTime`);
  - Ações de editar e excluir.
- Permitir criar um novo horário por meio de modal/formulário com seleção de playlist, dia da semana, hora de início e hora de término.
- Permitir editar um horário existente preenchendo o formulário com os valores atuais.
- Permitir excluir um horário com confirmação antes de enviar `DELETE /schedules/{uuid}`.
- Validar no formulário que todos os campos obrigatórios foram preenchidos e que `endTime` é posterior a `startTime` (espelhando a regra do `ScheduleService`), exibindo feedback de erro amigável caso a validação falhe ou a API retorne erro.

## Regras

- Não alterar, criar ou remover endpoints, DTOs, entidades ou regras no back-end.
- Seguir o padrão visual, de componentes, tipagem e testes já adotado nas telas de `arquivos` e `playlists`.
- Formatar horários compatíveis com `LocalTime` do back-end (`HH:mm:ss` ou `HH:mm`).

## Fora do escopo

- Qualquer alteração no back-end (ex.: detecção de conflito/sobreposição de horários, novos filtros ou paginação).
- Aplicação automática ou manual de configuração no Liquidsoap a partir desta tela, caso não faça parte do fluxo atual de `/schedules`.
- Criação ou edição de playlists diretamente na tela de programação.

## Critérios de aceite

- [ ] A rota `/programacao` existe, é protegida por autenticação e aparece na navegação lateral.
- [ ] A tela lista os horários retornados por `GET /schedules`, agrupados por dia da semana e ordenados por horário de início.
- [ ] É possível criar um novo horário (`POST /schedules`) selecionando playlist, dia da semana, início e fim.
- [ ] É possível editar um horário existente (`PUT /schedules/{uuid}`).
- [ ] É possível excluir um horário (`DELETE /schedules/{uuid}`) com confirmação.
- [ ] O formulário impede ou sinaliza envio quando `endTime` não é posterior a `startTime`.
- [ ] Estados de carregamento, erro (com retry) e lista vazia são exibidos corretamente.
- [ ] Testes unitários/de componente cobrem a camada de API e os fluxos principais da tela de programação.
