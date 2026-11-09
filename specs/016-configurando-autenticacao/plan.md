# Plano de implementação - Spec 016

## Objetivo

Implementar o login de usuários por `username` e `password`, armazenar senhas
com hash e devolver os tokens JWT de acesso e renovação gerados pelo
`JwtService`.

## Decisões de implementação

- Criar `AuthService`, seguindo a convenção de nomes de classes Java do projeto,
  com um único método público `auth`.
- Expor o login em `POST /login` por meio de `AuthController` e liberar somente
  esse endpoint na configuração do Spring Security.
- Criar um DTO próprio de autenticação com `username` e `password`, evitando
  acoplar o contrato de login aos demais campos de `UserRequestDTO`.
- Buscar o usuário por `username` no `UserRepository` e validar a senha
  informada contra o hash persistido com o `PasswordEncoder` do Spring
  Security.
- Responder credenciais desconhecidas ou senha incorreta de forma uniforme com
  `401 Unauthorized`, sem revelar qual campo falhou.
- Retornar diretamente o `Map<String, String>` produzido por
  `JwtService.generateTokens`, preservando as chaves existentes `accessToken`
  e `refreshToken` no JSON.
- Configurar um `PasswordEncoder` baseado em BCrypt como bean reutilizável pela
  autenticação e pelo cadastro de usuários, sem adicionar dependências.
- Garantir que novas senhas sejam codificadas antes da primeira persistência e
  que valores já codificados não sejam codificados novamente durante uma
  atualização sem troca de senha.
- Tornar `username` e `password` opcionais no contrato de atualização: o
  `UserMapper` só substituirá esses campos quando o DTO trouxer valores não
  nulos; no cadastro, as restrições atuais da entity e do banco continuarão
  exigindo os valores.
- Manter `fullname` e `nickname` com o comportamento atual, pois a spec torna
  opcionais somente `username` e `password`.

## Etapas

1. Criar o DTO de credenciais e adicionar a consulta por `username` ao
   `UserRepository`.
2. Configurar o `PasswordEncoder` e ajustar a criação de `User` para persistir
   a senha com hash.
3. Atualizar `UserRequestDTO` e `UserMapper` para documentar e aplicar a
   atualização opcional de `username` e `password`.
4. Criar `AuthService` para localizar o usuário, validar a senha e delegar a
   geração dos tokens ao `JwtService`.
5. Criar `AuthController` com `POST /login`, documentar o contrato no OpenAPI e
   liberar a rota na `SecurityFilterChain`.
6. Criar ou atualizar testes unitários de entity/mapper, repository, services,
   controller e configuração de segurança para cobrir os novos contratos.
7. Criar `tasks.md` e atualizar `docs/project-structure.md` com os novos
   componentes estruturais.
8. Executar a suíte completa de testes e validar a compilação Maven.

## Critérios de validação

- Uma senha nova é persistida como hash BCrypt, nunca em texto puro.
- Atualizar um usuário sem `username` ou `password` preserva os valores já
  armazenados.
- Credenciais válidas em `POST /login` retornam status 200 e um JSON contendo
  somente `accessToken` e `refreshToken`.
- Usuário inexistente e senha incorreta retornam `401 Unauthorized`.
- `POST /login` é público, enquanto os demais endpoints continuam protegidos.
- O projeto compila e todos os testes passam.
