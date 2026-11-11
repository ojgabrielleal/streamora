# Plano de implementação — Spec 018

## Objetivo

Criar o cadastro da grade de programação de playlists, permitindo registrar
em quais dias da semana e intervalos de horário cada playlist deve ser
executada, sem implementar agendamento ou execução automática de tarefas.

## Decisões de implementação

- Criar a tabela `playlist_schedules`, seguindo a convenção de nomes SQL do
  projeto, com as colunas `id`, `uuid`, `playlist_id`, `day_of_week`,
  `start_time` e `end_time`.
- Representar `day_of_week` por um enum Java persistido como texto, evitando
  valores numéricos pouco expressivos e mantendo o contrato da API legível.
- Usar o tipo SQL `TIME` e o tipo Java `LocalTime` para os limites do intervalo.
- Relacionar cada registro a uma `Playlist` por uma chave estrangeira
  `playlist_id` e mapear em `Playlist` a coleção inversa de horários.
- Gerar o UUID da entity em um método anotado com `@PrePersist`, preservando um
  UUID previamente definido e seguindo a convenção das entities existentes.
- Expor a playlist nos contratos por seu UUID, sem revelar IDs internos do
  banco de dados. A resposta de um horário incluirá um resumo da playlist, e a
  resposta de uma playlist incluirá seus horários.
- Usar DTOs sem referências recursivas: o resumo da playlist presente no
  horário não conterá novamente a coleção de horários.
- Validar no service a existência da playlist ao criar ou atualizar um horário
  e rejeitar intervalos cujo horário final não seja posterior ao inicial.
- Implementar os endpoints REST de listagem, consulta individual, criação,
  atualização e exclusão. Embora a spec enumere `index`, `store`, `update` e
  `delete`, a consulta individual será incluída para manter o CRUD e o padrão
  dos recursos existentes completos.
- Manter o controller restrito à delegação para o service e documentar tags,
  operações, parâmetros, corpos e respostas com anotações OpenAPI.

## Etapas

1. Criar a migration `V5__create_playlist_schedules_table.sql`, contendo apenas
   a criação de `playlist_schedules`, sua restrição de UUID único e a chave
   estrangeira para `playlists`.
2. Criar o enum do dia da semana e a entity `PlaylistSchedule`, incluindo seus
   mapeamentos JPA, relacionamento com `Playlist`, coleção inversa em
   `Playlist` e geração de UUID.
3. Criar `PlaylistScheduleRepository` com busca por UUID e disponibilizar no
   repositório de playlists a resolução da playlist informada pelo cliente.
4. Criar os DTOs imutáveis de requisição e resposta e o mapper para criação,
   atualização e conversão da entity; atualizar também o DTO e o mapper de
   playlist para retornarem seus horários sem formar referências recursivas.
5. Criar `PlaylistScheduleService` com `index`, `show`, `store`, `update` e
   `delete`, tratamento de recursos inexistentes e validação do intervalo.
6. Criar `PlaylistScheduleController`, delegando cada endpoint diretamente ao
   service e documentando a API no Swagger/OpenAPI.
7. Criar factories e testes unitários para entity, mapper, repository, service,
   controller e documentação OpenAPI, atualizando somente os testes globais de
   migrations exigidos pelas convenções do projeto.
8. Atualizar `docs/database-schema.md` com a nova tabela e seu relacionamento e
   `docs/project-structure.md` com os novos componentes e testes.
9. Criar `tasks.md` para acompanhar a execução da spec.
10. Executar todos os testes e verificar a compilação do projeto.

## Validação esperada

- A migration cria somente `playlist_schedules` e respeita as convenções de
  chave primária, UUID e chave estrangeira do projeto.
- Cada grade referencia uma playlist existente pelo UUID recebido na API e
  persiste internamente a chave estrangeira para o ID da playlist.
- Ao consultar ou listar horários, cada item identifica a playlist relacionada.
- Ao consultar uma playlist, a resposta inclui todos os seus horários.
- O dia da semana e os horários de início e fim são persistidos e retornados
  corretamente pelos endpoints.
- Intervalos inválidos e UUIDs de playlist ou grade inexistentes são rejeitados
  sem persistir alterações.
- Os controllers apenas encaminham os dados aos respectivos métodos do service.
- Os novos contratos e endpoints aparecem na documentação OpenAPI.
- Não há mecanismo de agendamento, disparo de tarefas ou integração com o
  Liquidsoap nesta spec.
- A documentação estrutural e do banco representa o estado resultante.
- Todos os testes existentes e novos passam e o projeto compila.

