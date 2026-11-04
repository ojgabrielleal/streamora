# Tarefas — Spec 003

## Implementação

- [x] Criar a migration da tabela `users`.
- [x] Criar a migration da tabela `playlists`.
- [x] Criar a migration da tabela `files`.
- [x] Criar a migration da tabela `playlist_files`.
- [x] Manter as migrations na localização padrão `classpath:db/migration`.

## Testes e validação

- [x] Criar um teste global para os campos `id` e `uuid` nas migrations das tabelas principais.
- [x] Excluir tabelas associativas da verificação global de migrations.
- [x] Validar a configuração do Flyway.
- [x] Executar os testes Maven.
- [x] Compilar e empacotar a aplicação.
- [x] Executar as migrations no MySQL 8.4 e validar o schema criado.
- [x] Conferir os critérios de aceite da Spec 003.
