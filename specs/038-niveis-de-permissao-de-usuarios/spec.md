# Spec 038 - Níveis de permissão de usuários

## Objetivo

Implementar níveis de permissão (papéis/roles) para os usuários do sistema, garantindo que apenas usuários autorizados possam gerenciar outros usuários e suas senhas, e que um usuário comum possa alterar apenas os próprios dados.

## Contexto

Atualmente, a entidade `User` não possui nenhum campo de papel ou nível de permissão, e o `SecurityConfig` exige apenas que a requisição esteja autenticada (`.anyRequest().authenticated()`). Como consequência, qualquer usuário autenticado pode chamar os endpoints de `/user` (como `PUT /user/{uuid}`, `POST /user` e `DELETE /user/{uuid}`) e alterar dados ou a senha de qualquer outro usuário do sistema.

## Requisitos

### Modelo de permissões

- Definir os níveis de permissão do sistema (ex.: `ADMIN` e `OPERATOR` / `LOCUTOR`, ou conjunto explícito de roles) na entidade `User` e no banco de dados via migration.
- Garantir que o usuário administrador padrão de desenvolvimento e usuários existentes recebam um papel válido na migration/seed.
- Incluir o nível de permissão nos claims do token JWT (ou carregá-lo no contexto de segurança pelo filtro JWT) e expor o papel do usuário no `UserResponseDTO` e nos endpoints de autenticação/usuário.

### Controle de acesso nos endpoints de usuários

- Apenas usuários com permissão de administrador (`ADMIN`) podem listar todos os usuários (`GET /user`), criar novos usuários (`POST /user`), excluir usuários (`DELETE /user/{uuid}`) e alterar o nível de permissão de qualquer usuário.
- Um usuário autenticado pode consultar (`GET /user/{uuid}`) e atualizar (`PUT /user/{uuid}`) apenas o próprio perfil, não podendo alterar o próprio nível de permissão.
- Um usuário administrador (`ADMIN`) pode consultar e atualizar qualquer usuário, incluindo redefinir senha e alterar nível de permissão, respeitando as regras de proteção do próprio sistema.
- Requisições sem permissão adequada devem retornar HTTP `403 Forbidden`.

### Alteração de senha

- Impedir que um usuário não administrador altere a senha de outro usuário via `PUT /user/{uuid}` ou qualquer endpoint de atualização.
- Permitir que o próprio usuário altere sua própria senha (exigindo ou validando conforme o fluxo definido no plano) e que administradores possam redefinir senhas de outros usuários.

### Front-end

- Refletir o nível de permissão do usuário autenticado na interface: ocultar ou restringir o gerenciamento de usuários (listagem, criação, exclusão e edição de terceiros) para usuários sem permissão de administrador.
- Permitir que usuários não administradores acessem apenas a visualização/edição do próprio perfil, caso aplicável.

## Regras

- Não remover funcionalidades existentes para usuários administradores.
- Manter compatibilidade com a autenticação do Liquidsoap (`/liquidsoap/auth`) e FTP, sem quebrar o fluxo atual de locutores.
- Impedir que o último administrador ativo do sistema seja excluído ou tenha seu papel rebaixado.
- Seguir os padrões de código Java (`docs/code-standards.md`) e de banco de dados (`docs/database-standards.md`).

## Fora do escopo

- Permissões granulares por recurso além dos papéis definidos (ACL por playlist, arquivo ou schedule individual).
- Cadastro público (self-registration) de novos usuários.
- Recuperação de senha por e-mail.

## Critérios de aceite

- [x] A entidade `User` e a tabela `users` possuem o campo de nível de permissão persistido via migration.
- [x] O token JWT e o `SecurityContext` refletem as authorities/roles do usuário autenticado.
- [x] Um usuário não administrador que tentar alterar a senha ou os dados de outro usuário recebe `403 Forbidden`.
- [x] Um usuário não administrador que tentar criar ou excluir usuários recebe `403 Forbidden`.
- [x] Um usuário não administrador consegue atualizar seus próprios dados (exceto seu nível de permissão).
- [x] Um usuário administrador consegue gerenciar usuários e permissões normalmente, sem conseguir remover ou rebaixar o último administrador.
- [x] A interface web respeita o nível de permissão do usuário logado ao exibir menus e ações de gerenciamento de usuários.
