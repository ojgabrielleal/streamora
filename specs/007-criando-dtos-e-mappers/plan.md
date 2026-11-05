# Plano de implementação - Spec 007

## Objetivo

Criar DTOs de entrada e saída e mappers para `User`, `Playlist` e
`PlaylistFile`, mantendo as entities restritas à camada de persistência.

## Contratos propostos

- `UserRequestDTO`: `fullname`, `nickname`, `username` e `password`.
- `UserResponseDTO`: `uuid`, `fullname`, `nickname` e `username`.
- `PlaylistRequestDTO`: `name`.
- `PlaylistResponseDTO`: `uuid`, `name` e os UUIDs dos arquivos associados.
- `PlaylistFileRequestDTO`: `type`, `name` e `path`.
- `PlaylistFileResponseDTO`: `uuid`, `type`, `name`, `path` e os UUIDs das
  playlists associadas.

Os DTOs serão records imutáveis. IDs internos não serão expostos e a senha do
usuário não fará parte da resposta. Relacionamentos serão representados por
UUIDs para evitar ciclos entre playlists e arquivos.

## Etapas

1. Criar os DTOs em `com.streamora.streamora.dto`, separados em requests e
   responses por entidade.
2. Criar `UserMapper`, `PlaylistMapper` e `PlaylistFileMapper` em
   `com.streamora.streamora.mapper`.
3. Implementar em cada mapper exatamente as duas conversões previstas pela
   spec: entity para response e request para uma nova entity.
4. Criar testes unitários para validar todos os campos, a omissão dos IDs
   internos, a conversão dos relacionamentos em UUIDs e a conversão dos DTOs
   de request em instâncias das entities existentes.
5. Atualizar `docs/project-structure.md` com os novos pacotes e testes.
6. Executar a suíte completa de testes e a compilação Maven.

## Extensão solicitada

Aplicar os DTOs e mappers aos services existentes:

- `index` e `show` retornam DTOs de response;
- `store` e `update` recebem DTOs de request e retornam DTOs de response;
- as operações de associação de arquivos a playlists retornam
  `PlaylistFileResponseDTO`;
- `delete` continua sem retorno;
- os testes dos services passam a validar os contratos DTO e o mapeamento,
  mantendo as repositories isoladas por mocks.

## Fora do escopo

- Criar controllers.
- Adicionar validações de entrada.
- Adicionar bibliotecas de mapeamento.
- Alterar entities, migrations ou o esquema do banco.

## Validação

- Todos os mappers possuem os dois métodos exigidos.
- Os testes dos mappers passam.
- A suíte existente continua passando.
- O projeto compila sem novas dependências.
