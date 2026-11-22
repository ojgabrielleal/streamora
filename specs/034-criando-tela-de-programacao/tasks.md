# Tarefas - Spec 034

- [x] Criar `schedule-types.ts`, `schedule-api.ts` e `schedule-queries.ts` em `front-end/src/api/schedules/`.
- [x] Adicionar o destino Programação (`/programacao`) em `AuthenticatedLayout`, `DashboardSidebar` e `router.tsx`.
- [x] Implementar a listagem da grade de programação por dia da semana com estados de carregamento, erro e vazio.
- [x] Implementar criação, edição e exclusão de horários com validação de intervalo (`endTime > startTime`) e confirmação de exclusão.
- [x] Criar `front-end/src/routes/schedules.tsx` integrando a tela.
- [x] Implementar testes unitários da API de schedules e testes da tela de programação.
- [x] Executar validações do front-end (testes, lint e build).
- [x] Revisar os critérios de aceite.

## Validação

- `cd front-end && npm test`: 16 arquivos de teste, 52 testes passaram.
- `cd front-end && npm run lint`: 0 erros (1 warning de `react(only-export-components)` para `WEEK_DAYS`).
- `cd front-end && npm run build`: `tsc -b && vite build` concluído com sucesso.
