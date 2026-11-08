# Spec 015 - Implementando filtro de autenticação

**Status:** Concluída

## Objetivo

Implementar filtro JWT de autenticação para barrar qualquer requisição onde o token não esteja dado como valido pelo JwtService

## Requisitos
- Criar e configurar um filtro na pasta `src/main/java/com/streamora/streamora/filter` que verifique se a requisição possui o header `Bearer` com um token.
- O token enviado na requisição deve ser do tipo `accessToken`, ele deve ser verificado pelo método `validateAccessToken` no `JwtService`.
- Em caso de token invalido deve retornar o status HTTP correspondente
