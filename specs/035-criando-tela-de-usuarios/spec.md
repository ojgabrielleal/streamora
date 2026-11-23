# Spec 035 - Criando tela de usuários

**Status:** Concluída

## Objetivo

Criar a tela de gerenciamento de usuários do painel web do Streamora, permitindo listar, criar, editar e excluir usuários integrados à API `/user`.

## Contexto

O backend já disponibiliza os endpoints de gerenciamento de usuários em `/user` (`GET`, `POST`, `PUT /{uuid}`, `DELETE /{uuid}`) e o frontend já possui autenticação, layout autenticado e telas de gerenciamento para arquivos, playlists e programação. Atualmente não existe uma interface no painel para administrar os usuários do sistema.

## Requisitos

### Rota e navegação

- Adicionar a rota autenticada `/usuarios` no roteador da aplicação.
- Exibir o item de navegação `Usuários` no menu lateral do painel autenticado.

### Listagem de usuários

- Consumir `GET /user` para listar os usuários cadastrados.
- Exibir estados de carregamento, erro (com opção de tentar novamente) e lista vazia.
- Apresentar os dados principais de cada usuário (como nome, e-mail/identificador e perfil/status, conforme retornado pela API) e ações de editar e excluir.

### Criação e edição de usuário

- Permitir criar um novo usuário via `POST /user` a partir de formulário/modal com validação dos campos obrigatórios.
- Permitir editar um usuário existente via `PUT /user/{uuid}` com preenchimento dos dados atuais.
- Atualizar a listagem automaticamente após criação ou edição e exibir feedback de sucesso ou erro.

### Exclusão de usuário

- Permitir excluir um usuário via `DELETE /user/{uuid}` mediante confirmação explícita.
- Atualizar a listagem automaticamente após a exclusão e exibir feedback de sucesso ou erro.

## Regras

- Seguir o padrão visual, de componentes e de organização de API/hooks (TanStack Query) já adotado nas telas do frontend.
- Não alterar endpoints ou contratos do backend nesta spec.

## Fora do escopo

- Alterações no backend ou novos endpoints de usuário.
- Controle granular de permissões por tela ou recuperação de senha por e-mail.

## Critérios de aceite

- [x] A rota `/usuarios` está registrada, protegida por autenticação e acessível pelo menu lateral.
- [x] A tela lista os usuários retornados por `GET /user` com estados de carregamento, erro e vazio.
- [x] É possível criar, editar e excluir usuários com atualização da lista e feedback ao usuário.
- [x] Os testes automatizados do frontend passam para as novas funcionalidades e rotas.
