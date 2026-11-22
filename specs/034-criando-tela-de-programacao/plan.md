# Plano de implementação - Spec 034

## Objetivo

Implementar a tela de Programação no front-end (`/programacao`) conectada aos endpoints existentes de `/schedules` e `/playlists`, mantendo consistência arquitetural e visual com as telas de Arquivos e Playlists.

## Decisões de implementação

- Reutilizar `apiClient` e `playlistsQueryOptions` já existentes no front-end, sem modificar nada no back-end.
- Organizar a camada de dados em `front-end/src/api/schedules/` (`schedule-types.ts`, `schedule-api.ts`, `schedule-queries.ts`).
- Organizar os componentes de interface em `front-end/src/components/schedules/` e a página em `front-end/src/routes/schedules.tsx`.
- Usar `WeekDay` (`MONDAY`..`SUNDAY`) exatamente como definido no back-end, mapeando para rótulos em português apenas na camada de apresentação.
- Garantir que os inputs de hora enviem formato aceito por `LocalTime` (`HH:mm` / `HH:mm:ss`).

## Estrutura proposta

- `front-end/src/api/schedules/schedule-types.ts`
- `front-end/src/api/schedules/schedule-api.ts`
- `front-end/src/api/schedules/schedule-queries.ts`
- `front-end/src/api/schedules/schedule-api.test.ts`
- `front-end/src/components/schedules/schedule-board.tsx`
- `front-end/src/components/schedules/schedule-form-modal.tsx`
- `front-end/src/routes/schedules.tsx`
- `front-end/src/router.tsx` (adição da rota `/programacao`)
- `front-end/src/components/layout/authenticated-layout.tsx` e `front-end/src/components/dashboard/dashboard-sidebar.tsx` (adição do item Programação)

## Etapas

1. Criar os tipos, funções de API e `queryOptions` de schedules em `front-end/src/api/schedules/`.
2. Adicionar o destino de navegação "Programação" (`/programacao`) em `AuthenticatedLayout`, `DashboardSidebar` e `router.tsx`.
3. Implementar o componente de grade (`ScheduleBoard`) com agrupamento por dia da semana, ordenação por `startTime` e estados de loading, erro e vazio.
4. Implementar o modal/formulário de criação e edição (`ScheduleFormModal`) consumindo `playlistsQueryOptions()` e validando `endTime > startTime`.
5. Implementar a ação de exclusão com confirmação e invalidação de cache do TanStack Query.
6. Criar a rota `front-end/src/routes/schedules.tsx` integrando layout, query e componentes.
7. Escrever testes para `schedule-api.ts` e para os fluxos da tela de programação.

## Estratégia de testes

- Testes unitários para `schedule-api.ts` verificando chamadas `GET`, `POST`, `PUT` e `DELETE` para `/schedules`.
- Testes de componente/rota verificando listagem por dia da semana, estados de loading/erro/vazio, criação, edição, exclusão e validação de horário.
- Testes de roteamento verificando proteção da rota `/programacao`.

## Validação

- Executar os testes do front-end (`npm test` ou `vitest run` em `front-end/`).
- Executar verificação de tipos e build do front-end (`npm run build` em `front-end/`).

## Riscos e cuidados

- Garantir compatibilidade do formato de hora enviado pelo `<input type="time">` (`HH:mm`) com `LocalTime` do Spring Boot.
- Tratar corretamente erros retornados pelo back-end (ex.: `400` quando `endTime <= startTime` ou `404` quando playlist/schedule não existe).

## Fora do escopo

- Alterações no back-end Java, migrations ou integração direta com Liquidsoap.
