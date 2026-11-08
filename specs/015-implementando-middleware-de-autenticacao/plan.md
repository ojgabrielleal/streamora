# Plano de implementação - Spec 015

## Objetivo

Adicionar autenticação stateless por JWT à cadeia do Spring Security, aceitando
somente access tokens válidos nos endpoints protegidos.

## Decisões de implementação

- Criar o filtro no pacote `com.streamora.streamora.filter`, seguindo o
  pacote-base real do projeto.
- Implementar o filtro como `OncePerRequestFilter` para que cada requisição
  seja verificada uma única vez.
- Extrair o token exclusivamente do header `Authorization` no formato
  `Bearer <token>`.
- Usar `JwtService.validateAccessToken`, que é o método já existente e
  equivalente ao `verifyAccessToken` citado pela spec.
- Deixar requisições sem Bearer token seguirem para as regras de autorização
  do Spring Security, que respondem com `401 Unauthorized` quando o endpoint é
  protegido.
- Responder diretamente com `401 Unauthorized` quando um Bearer token estiver
  presente, mas for inválido, expirado ou não for um access token.
- Registrar uma autenticação no `SecurityContext` quando o access token for
  válido e continuar a cadeia de filtros.
- Preservar o acesso público à documentação OpenAPI/Swagger exclusivamente
  pelas regras da configuração do Spring Security.

## Etapas

1. Criar `JwtAuthenticationFilter` com extração do Bearer token,
   validação pelo `JwtService` e preenchimento do `SecurityContext`.
2. Configurar o filtro JWT antes do filtro de autenticação padrão na
   `SecurityFilterChain`, definir a sessão como stateless e adequar a cadeia ao
   uso de autenticação por token.
3. Criar testes unitários do filtro cobrindo header ausente, esquema
   incorreto, token vazio, token inválido e token válido.
4. Atualizar os testes de configuração para comprovar que endpoints protegidos
   rejeitam requisições sem JWT, aceitam access token válido e mantêm as rotas
   públicas acessíveis.
5. Atualizar `docs/project-structure.md` para registrar os novos pacotes e
   testes de filtro.
6. Criar `tasks.md` com as tarefas executadas e validar a implementação com a
   suíte completa de testes e compilação Maven.

## Critérios de validação

- Requisição protegida sem `Authorization: Bearer <token>` retorna 401.
- Access token inválido, expirado ou malformado retorna 401.
- Refresh token usado como Bearer retorna 401.
- Access token válido autentica a requisição e permite chegar ao endpoint.
- Endpoints públicos de documentação continuam sem exigir token.
- O projeto compila e todos os testes passam.
