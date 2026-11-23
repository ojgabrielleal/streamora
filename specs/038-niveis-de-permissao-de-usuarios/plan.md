# Plano de Implementação — Spec 038: Níveis de permissão de usuários

## 1. Modelo de Dados e Migration
- **Enum `UserRole` (`ADMIN`, `STANDARD`)**: criar enum para representar os níveis de permissão do sistema.
- **Entidade `User`** (`src/main/java/com/streamora/streamora/entity/User.java`): adicionar campo `role` (`@Enumerated(EnumType.STRING)`, `nullable = false`), com valor padrão `STANDARD` caso não informado na criação por código.
- **Migration Flyway (`V10__add_role_to_users_table.sql`)**:
  - Adicionar coluna `role` na tabela `users` (`VARCHAR(30) NOT NULL DEFAULT 'STANDARD'`).
  - Garantir que o usuário administrador padrão de desenvolvimento e eventuais administradores existentes recebam `ADMIN`.
- **Documentação**: atualizar `docs/database-schema.md`.

## 2. Autenticação, JWT e Segurança
- **`JwtService` e `JwtAuthenticationFilter`**:
  - Incluir a claim `role` no token JWT gerado no login/refresh.
  - No `JwtAuthenticationFilter`, extrair a role (ou carregar do `User`) e popular o `SecurityContextHolder` com `SimpleGrantedAuthority("ROLE_" + role.name())`.
- **`SecurityConfig`** (`src/main/java/com/streamora/streamora/config/SecurityConfig.java`):
  - Habilitar `@EnableMethodSecurity` para suportar `@PreAuthorize` nos controllers/services, ou configurar regras explícitas no `HttpSecurity`.
  - Manter `/liquidsoap/auth` e fluxo FTP funcionando sem alterações de compatibilidade.
- **DTOs e Mappers**:
  - Expor `role` em `UserResponseDTO` e na resposta de autenticação/usuário atual.
  - Aceitar `role` opcional em `UserRequestDTO` (aplicando regras de permissão no service).

## 3. Controle de Acesso e Regras de Negócio (`UserController` / `UserService`)
- **Restrições por endpoint** (`src/main/java/com/streamora/streamora/controller/UserController.java`):
  - `GET /user`: restrito a `ADMIN` (`403 Forbidden` para não-admins).
  - `POST /user`: restrito a `ADMIN` (`403 Forbidden` para não-admins).
  - `DELETE /user/{uuid}`: restrito a `ADMIN` (`403 Forbidden` para não-admins).
  - `GET /user/{uuid}`: `ADMIN` pode consultar qualquer usuário; `STANDARD` apenas o próprio `uuid` (`403 Forbidden` caso contrário).
  - `PUT /user/{uuid}`: `ADMIN` pode atualizar qualquer usuário; `STANDARD` apenas o próprio `uuid` (`403 Forbidden` caso contrário).
- **Proteções no `UserService`**:
  - **Alteração de papel (`role`)**: apenas `ADMIN` pode alterar o papel de um usuário; se um `STANDARD` tentar alterar o próprio `role`, retornar `403 Forbidden`.
  - **Alteração de senha**:
    - `STANDARD` só pode alterar a própria senha (nunca a de terceiros, pois `PUT /user/{uuid}` para outro `uuid` retorna `403`).
    - `ADMIN` pode redefinir a senha de qualquer usuário.
  - **Proteção do último administrador**: impedir exclusão (`DELETE /user/{uuid}`) ou rebaixamento (`ADMIN` → `STANDARD`) quando houver apenas 1 usuário `ADMIN` ativo no sistema.

## 4. Front-end
- **Tipos e Auth**: atualizar `front-end/src/api/auth/auth-types.ts` e o estado/contexto de autenticação para armazenar e expor `role` do usuário logado.
- **Navegação e Layout** (`front-end/src/components/layout/authenticated-layout.tsx`): ocultar/restringir o item de menu de gerenciamento de usuários para não-administradores (ou exibir apenas acesso ao próprio perfil).
- **Tela de Usuários** (`front-end/src/components/users/users-board.tsx` e `front-end/src/routes/users.tsx`):
  - Para `ADMIN`: manter listagem, criação, edição (incluindo seleção de `role` e redefinição de senha) e exclusão.
  - Para `STANDARD`: restringir listagem/criação/exclusão de terceiros e permitir apenas visualização/edição do próprio perfil (sem seletor de `role`).

## 5. Testes e Validação
- **Backend**: testes unitários/integração cobrindo:
  - `STANDARD` recebendo `403` ao tentar listar, criar, excluir ou editar/alterar senha de outro usuário.
  - `STANDARD` conseguindo consultar e atualizar o próprio perfil (inclusive própria senha), mas impedido de alterar seu `role`.
  - `ADMIN` gerenciando usuários normalmente e sendo impedido de excluir/rebaixar o último `ADMIN`.
  - Compatibilidade com `/liquidsoap/auth`.
- **Front-end**: atualizar/adicionar testes em `users-board.test.tsx` e testes de auth para validar renderização condicional por `role`.
- **Execução**: rodar `./mvnw test` no backend e suíte de testes/build no `front-end`.
