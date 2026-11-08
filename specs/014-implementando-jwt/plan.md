# Plano de implementação — Spec 014

## Objetivo

Implementar a geração e a validação de access tokens e refresh tokens JWT,
assinados por uma chave configurada no ambiente e contendo a identidade do
usuário e um identificador imutável por token.

## Etapas

1. Adicionar ao Maven uma biblioteca JWT mínima, compatível com a versão atual
   do Java e do Spring Boot, sem alterar versões já existentes.
2. Expor a configuração `JWT_SECRET` no `.env.example` e mapeá-la em
   `application.properties`, mantendo a chave real fora do repositório.
3. Criar `JwtService` no pacote `service`, responsável por gerar e validar os
   dois tipos de token.
4. Implementar `generateTokens(String username)` para chamar os métodos
   privados `generateAccessToken` e `generateRefreshToken` e retornar um
   `Map<String, String>` com as chaves `accessToken` e `refreshToken`.
5. Gerar cada token com o username no subject, um identificador `jti` único e
   imutável, data de emissão, data de expiração e uma indicação explícita do
   tipo do token.
6. Configurar a expiração do access token em 5 minutos e a do refresh token em
   7 dias.
7. Implementar `validateAccessToken` e `validateRefreshToken`, delegando a
   validação comum de assinatura, expiração, username, `jti` e tipo para um
   método privado.
8. Criar testes unitários para geração, claims, prazos, unicidadn.e do `jti`,
   assinatura, expiração, adulteração e separação entre access e refresh toke
9. Atualizar `docs/project-structure.md` para registrar o novo service e sua
   classe de testes.
10. Executar todos os testes e validar a compilação Maven, corrigindo somente
    problemas relacionados ao escopo da spec.

## Decisões de implementação

- Usar `JWT_SECRET` como variável de ambiente, referenciada pela propriedade
  `jwt.secret`; nenhum segredo real será versionado.
- Manter os prazos definidos no código do service como constantes: 5 minutos
  para access token e 7 dias para refresh token.
- Usar o claim padrão `sub` para o username e o claim padrão `jti` para o
  identificador, criando um UUID novo uma única vez durante a emissão de cada
  token.
- Adicionar um claim privado `token_type`, com os valores `access` e `refresh`,
  para que cada método de validação aceite somente o tipo correspondente.
- Definir `generateTokens(String username)` como API pública de geração e
  `validateAccessToken(String token)`/`validateRefreshToken(String token)` como
  APIs públicas que retornam `boolean`.
- Considerar inválido um token malformado, adulterado, expirado, assinado com
  outra chave, sem username, sem `jti` ou com tipo incompatível.
- Não criar endpoints, filtros de autenticação, persistência de refresh tokens
  ou integração com o `SecurityFilterChain`, pois não fazem parte da spec.

## Validação esperada

- Os dois tokens são assinados exclusivamente pela chave configurada em
  `JWT_SECRET`.
- Ambos preservam o username informado e possuem `jti` não vazio e distinto.
- O access token expira 5 minutos após sua emissão e o refresh token, 7 dias
  após sua emissão.
- `generateTokens` retorna exatamente as chaves `accessToken` e
  `refreshToken`.
- Cada validador aceita um token válido do tipo correto e rejeita tokens
  inválidos ou do tipo oposto.
- Todos os testes existentes continuam passando e o projeto compila com
  sucesso.
