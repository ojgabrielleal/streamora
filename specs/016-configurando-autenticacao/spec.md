# Spec 016 - Configurando autenticação

**Status:** Concluída

## Objetivo

Criar e configurar service e controller de autenticação do usuário

## Requisitos
- Criar um novo service chamado `authService` na pasta `src/main/java/com/streamora/streamora/service`, esse service terá o método chamado `auth` que servirá apenas para fazer o login.
- Atualizar a entity `user` e criar um método que vai hashear o password a cada vez que um valor novo for criado na tabela.
- Atualizar `userMapper` no método `toEntity` e `userRequestDTO` colocando os campos `username` e `password` como opicionais para evitar que ao atualizar o usuário seja sempre pedido esses campos.
- Ao fazer login no método `auth` dentro de `authService` a resposta para o usuário deverá ser um `JSON` contendo os tokens de `access` e `refresh`, essa resposta irá vim do método `generateTokens` que está dentro de `JwtService`.

## Critérios de aceite

- [x] Ao fazer login no método `auth` dentro de `authService` a resposta para o usuário deverá ser um `JSON` contendo os tokens de `access` e `refresh`, essa resposta irá vim do método `generateTokens` que está dentro de `JwtService`.
