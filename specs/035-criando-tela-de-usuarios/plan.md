# Plano de implementação - Spec 035

## Objetivo

Implementar a tela de gerenciamento de usuários em `/usuarios` no frontend, integrada aos endpoints `/user` do backend com TanStack Query e seguindo o padrão visual e estrutural das telas existentes.

## Decisões de implementação

- **Camada de API (`front-end/src/api/users/`)**: criar cliente HTTP, tipos (`UserResponse`, `UserRequest`) e hooks TanStack Query (`useUsers`, `useCreateUser`, `useUpdateUser`, `useDeleteUser`) invalidando a query key de usuários após mutações.
- **Campos do formulário**: alinhar com `UserRequestDTO` (`fullname`, `nickname`, `username`, `password`), exigindo `username` e `password` na criação e mantendo-os opcionais (com `username` desabilitado/imutável) na edição.
- **Rota e navegação**: registrar `/usuarios` em `front-end/src/router.tsx` com `beforeLoad: requireAccessToken` e adicionar o item `Usuários` em `front-end/src/components/dashboard/dashboard-sidebar.tsx`.
- **Feedback e confirmação**: exibir mensagens de erro/sucesso e confirmação explícita antes de excluir um usuário.

## Estrutura proposta

- `front-end/src/api/users/types.ts` — tipos `UserResponse` e `UserRequest`.
- `front-end/src/api/users/users-api.ts` — funções de chamada à API `/user`.
- `front-end/src/api/users/use-users.ts` — hooks `useUsers`, `useCreateUser`, `useUpdateUser`, `useDeleteUser`.
- `front-end/src/components/users/users-board.tsx` — componente principal da tela (listagem, estados de carregamento/erro/vazio, modal/formulário de criação/edição e confirmação de exclusão).
- `front-end/src/routes/users.tsx` — página da rota `/usuarios` usando o layout autenticado.
- `front-end/src/router.tsx` — registro da rota `/usuarios`.
- `front-end/src/components/dashboard/dashboard-sidebar.tsx` — item `Usuários` no menu lateral.
- `front-end/src/components/users/users-board.test.tsx` — testes unitários/renderização da tela de usuários.
- `front-end/src/components/dashboard/dashboard.test.tsx` — verificação do link `/usuarios` na sidebar.

## Etapas

1. Criar tipos, funções de API e hooks TanStack Query em `front-end/src/api/users/`.
2. Criar o componente `UsersBoard` em `front-end/src/components/users/users-board.tsx` com listagem, formulário de criação/edição e confirmação de exclusão.
3. Criar a rota `front-end/src/routes/users.tsx`, registrar `/usuarios` em `front-end/src/router.tsx` e adicionar o link no `dashboard-sidebar.tsx`.
4. Escrever e atualizar os testes em `users-board.test.tsx` e `dashboard.test.tsx`.
5. Executar testes e build do frontend para validar a entrega.

## Estratégia de testes

- Testar renderização da listagem, estado de carregamento, estado de erro com retry e estado vazio em `users-board.test.tsx`.
- Testar criação, edição e exclusão de usuário com mocks das mutações/API.
- Atualizar `dashboard.test.tsx` para garantir a presença do link `/usuarios` na sidebar.

## Validação

- `npm --prefix front-end test`
- `npm --prefix front-end run build`

## Riscos e cuidados

- Garantir que `username` não seja alterado na edição (conforme `UserRequestDTO`, é imutável e opcional no `PUT /user/{uuid}`).
- Garantir que `password` seja opcional na edição e não seja enviado vazio se o usuário não quiser alterar a senha.

## Fora do escopo

- Alterações no backend ou novos endpoints de usuário.
- Controle granular de permissões por tela ou recuperação de senha por e-mail.
