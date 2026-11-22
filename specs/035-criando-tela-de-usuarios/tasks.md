# Tarefas - Spec 035

- [x] Criar tipos, cliente HTTP e hooks TanStack Query em `front-end/src/api/users/`.
- [x] Implementar `UsersBoard` em `front-end/src/components/users/users-board.tsx` com listagem, criação, edição e exclusão com confirmação.
- [x] Criar `front-end/src/routes/users.tsx`, registrar `/usuarios` em `front-end/src/router.tsx` e adicionar `Usuários` em `dashboard-sidebar.tsx`.
- [x] Criar testes em `front-end/src/components/users/users-board.test.tsx` e atualizar `dashboard.test.tsx`.
- [x] Executar `npm --prefix front-end test` e `npm --prefix front-end run build`.
- [x] Revisar os critérios de aceite da `spec.md`.

## Validação

- `npm --prefix front-end test`: 17 arquivos de teste, 56 testes passando.
- `npm --prefix front-end run build`: `tsc -b && vite build` concluído com sucesso.
