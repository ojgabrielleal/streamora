# Plano de implementação - Spec 019

## Objetivo

Substituir o relacionamento muitos-para-muitos entre playlists e arquivos por
uma referência opcional ao nome de um arquivo M3U na própria playlist e
padronizar toda a nomenclatura de agendamentos como `Schedule`/`schedules`.

## Decisões de implementação

- Remover integralmente o domínio persistido de `PlaylistFile`: tabela `files`,
  entity, repository, service, controller, DTOs, mapper, enum, relacionamentos
  e testes exclusivos. O gerenciamento futuro trabalhará diretamente com os
  arquivos do FTP, sem representação no banco de dados.
- Representar `m3u_file_name` como coluna `VARCHAR(255)` anulável. O contrato
  aceitará `null` enquanto a geração do M3U não existir.
- Validar no mapper/contrato que, quando informado, `m3uFileName` seja somente
  um nome de arquivo, sem componentes de diretório.
- Renomear os artefatos Java e de teste de `PlaylistSchedule` para `Schedule`,
  preservando o comportamento atual dos agendamentos.
- Expor os agendamentos sob a rota `/schedules`, sem manter alias para a rota
  descontinuada.
- Adicionar quatro migrations independentes e sequenciais: remoção de
  `playlist_files`, remoção de `files`, adição de
  `playlists.m3u_file_name` e renomeação de `playlist_schedules` para
  `schedules`.

## Etapas

1. Criar testes para o novo campo de playlist, a ausência do relacionamento
   com arquivos, a nomenclatura `Schedule` e a rota `/schedules`.
2. Criar as migrations separadas, cobrindo sua presença e as convenções
   globais já verificadas pelo projeto.
3. Remover todos os componentes `PlaylistFile`, suas operações, contratos,
   relacionamentos, testes exclusivos e representações na documentação
   OpenAPI.
4. Atualizar `Playlist`, seus DTOs, mapper, service e testes para
   `m3uFileName`, removendo `fileUuids` e qualquer dependência de arquivos
   persistidos.
5. Renomear entity, repository, service, controller, DTOs, mapper, factory e
   testes de agendamento para `Schedule`, atualizando imports, mensagens,
   associações e rota HTTP.
6. Atualizar os testes estruturais, de contexto e OpenAPI para os componentes
   e contratos resultantes.
7. Atualizar `docs/database-schema.md` e `docs/project-structure.md`, removendo
   todas as referências descontinuadas; ajustar exemplos em documentação
   adicional caso apontem para APIs removidas.
8. Executar a suíte completa e a compilação, revisar todos os critérios de
   aceite e procurar referências residuais aos nomes antigos.

## Validação

- `./mvnw test` ou `mvn test`, conforme o executável disponível no projeto.
- Compilação do projeto pelo Maven.
- Inspeção das migrations para confirmar uma tabela alterada por arquivo e
  uso de `RENAME TABLE` para preservar os registros de agendamentos.
- Busca final por `PlaylistFile`, `playlist_files`, `PlaylistSchedule`,
  `playlist_schedules` e pelas rotas removidas.

## Fora de escopo

- Geração, alteração ou remoção física de arquivos M3U.
- Mudanças na configuração do Liquidsoap.
- Composição ponderada de playlists ou comportamento de fallback.
