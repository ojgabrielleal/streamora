# Spec 014 - Implementando JWT

**Status:** Concluída

## Objetivo

Implementar JWT para autenticação com configuração para access token e refresh token

## Requisitos
- Configurar um novo service na pasta dos services chamado `JwtService`. Esse service deve ter métodos privados, um método chamado `generateAccessToken` para gerar um`accessToken` e outro chamado `generateRefreshToken` para gerar um `refreshToken`.
- O service `JwtService` deve ter um método gerál público que vai se chamar `generateTokens`. Esse método irá chamar os métodos privados `generateAccessToken` e `generateRefreshToken` e devolver em formato `map` com as chaves `accessToken` e `refreshToken`.
- O service `JwtService` deve ter métodos chamados `validateAccessToken` e `validateRefreshToken`. Esses dois métodos serão responsáveis por validar cada um dos tokens.
- O `accessToken` deve ter um prazo de 5 minutos e o `refreshToken` de 7 dias.
- Cada token deve ser assinado pela chave default do JWT que deve ser inserida no .env.
- Cada token deve guardar o username do usuário e o id de cada token deve ser imutavel .

## Critérios de aceite

- [x] Cada token deve ser assinado pela chave default do JWT que deve ser inserida no .env.
- [x] Cada token deve guardar o username do usuário e o id de cada token deve ser imutavel.
- [x] O `accessToken` deve ter um prazo de 5 minutos e o `refreshToken` de 7 dias.
- [x] O service `JwtService` deve ter um método gerál público que vai se chamar `generateTokens`. Esse método irá chamar os métodos privados `generateAccessToken` e `generateRefreshToken` e devolver em formato `map` com as chaves `accessToken` e `refreshToken`.
- [x] O service `JwtService` deve ter métodos chamados `validateAccessToken` e `validateRefreshToken`. Esses dois métodos serão responsáveis por validar cada um dos tokens.
