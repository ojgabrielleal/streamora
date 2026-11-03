# Plano de implementação — Spec 003

## Objetivo

Criar as migrations iniciais do banco de dados para as tabelas `users`, `playlists`, `files` e `playlist_files`, mantendo uma tabela por arquivo SQL e usando o diretório convencional do Flyway.

## Premissas

- Cada tabela será criada por uma migration independente, respeitando a regra de uma tabela por arquivo.
- As migrations ficarão em `src/main/resources/db/migration`, o diretório convencional do Flyway no Spring Boot.
- O Flyway usará sua localização padrão `classpath:db/migration`, sem uma configuração redundante em `application.properties`.
- Os arquivos seguirão a convenção de versionamento do Flyway: `V1__create_users_table.sql`, `V2__create_playlists_table.sql`, `V3__create_files_table.sql` e `V4__create_playlist_files_table.sql`.
- As tabelas usarão o mecanismo `InnoDB` e o conjunto de caracteres `utf8mb4`, compatíveis com o MySQL 8.4 adotado pelo projeto.
- As colunas `id` serão `BIGINT UNSIGNED`, chaves primárias e auto incrementais.
- As colunas `uuid` serão `CHAR(36)`, obrigatórias e terão restrição de unicidade.
- Os campos de texto obrigatórios usarão `VARCHAR(255)`; o campo `password` também usará `VARCHAR(255)` para armazenar apenas o hash da senha.
- O campo `files.type` será um `ENUM` obrigatório com os valores `music`, `station_id`, `sweeper`, `jingle`, `commercial` e `bumper`.
- A tabela `playlist_files` terá chave primária composta por `playlist_id` e `file_id`, evitando associações duplicadas.
- As chaves estrangeiras de `playlist_files` serão obrigatórias, indexadas pela chave primária composta e usarão `ON DELETE CASCADE` e `ON UPDATE CASCADE` para preservar a integridade das associações.
- Nenhuma dependência ou versão será alterada.

## Etapas

1. Criar `tasks.md` com as tarefas de implementação e validação derivadas deste plano.
2. Criar a migration da tabela `users` com chave primária, UUID único e os campos de identificação e autenticação.
3. Criar a migration da tabela `playlists` com chave primária, UUID único e nome.
4. Criar a migration da tabela `files` com chave primária, UUID único, tipo enumerado, nome e caminho.
5. Criar a migration da tabela associativa `playlist_files` com chave primária composta e chaves estrangeiras para `playlists` e `files`.
6. Manter o Flyway usando sua localização padrão `classpath:db/migration`.
7. Criar testes automatizados que verifiquem a existência, a nomenclatura e a separação das migrations, além da localização configurada para o Flyway.
8. Executar as migrations contra MySQL 8.4 e validar a estrutura resultante das quatro tabelas.
9. Atualizar a spec e as tarefas com o status final e registrar a implementação em um commit.

## Validação

- Executar os testes existentes e os novos testes com `./mvnw test`.
- Compilar e empacotar a aplicação com `./mvnw package`.
- Validar a execução limpa das migrations em uma instância MySQL 8.4.
- Consultar o schema criado para confirmar colunas, tipos, chaves primárias, unicidade, enumeração e chaves estrangeiras.
- Confirmar que cada arquivo SQL cria ou altera somente a sua respectiva tabela.
- Confirmar que o Flyway encontra as migrations em `classpath:db/migration`.
- Conferir todos os critérios de aceite da `spec.md` e registrar qualquer limitação encontrada.

## Critérios de conclusão

- As quatro tabelas descritas na Spec 003 possuem migrations SQL próprias.
- Nenhuma migration cria ou altera mais de uma tabela.
- A aplicação está configurada para localizar e executar as migrations no diretório definido pela spec.
- A estrutura criada no MySQL corresponde às colunas, relações e restrições aprovadas neste plano.
- Os testes e a compilação terminam com sucesso, ou eventuais limitações ficam documentadas.
- O status da spec e das tarefas está atualizado e a implementação está registrada em um commit na branch indicada pela spec.
