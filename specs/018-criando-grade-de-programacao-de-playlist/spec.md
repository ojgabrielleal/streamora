# Spec 018 - Criar grade de programação de playlists

**Status:** Concluída

## Objetivo

Criar toda estrutura para fazer uma grade de programação na qual será possível registrar os horários na qual playlists devem ser executadas. 

## Requisitos
- Criar uma nova tabela no banco de dados chamada `playlistSchedules` 
- A estrutura da tabela deve ter uma coluna que vai ter como FK a tabela `playlists`
- Criar uma nova `entity` para a tabela com um método que faça a geração automática de `uuid` com a notação `prePersist` 
- Criar um novo `repository` com método personalizado para busca por `uuid`
- Criar novos `mappers` e `dtos` seguindo o padrão do projeto.
- Criar um novo `service` com métodos `CRUD` padrões: index, store, update, delete. E implementar os `mappers` e `dtos` criados.
- Criar um novo `controller` onde cada método vai apenas chamar seu respectivo método no `service`
- Tudo deve ser documentado para ser exibido no `swagger` da `OpenAPI`

## Critérios de aceite 
- [x] A estrutura da tabela deve ter uma coluna que vai ter como FK a tabela `playlists`
- [x] A nova tabela não deve ter nada demais, é apenas um registro de horários sem função de agendamento de tarefas.
- [x] A estrutura da nova tabela deve ser registrada no `database-schema.md`
