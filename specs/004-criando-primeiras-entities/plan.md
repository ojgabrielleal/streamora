# Plano de implementação — Spec 004

## Objetivo

Criar as primeiras entities JPA do projeto com base nas migrations existentes para `users`, `playlists`, `files` e `playlist_files`.

## Premissas

- As entities ficarão em `src/main/java/com/streamora/streamora/entity`.
- Serão criadas as entities `User`, `Playlist` e `PlaylistFile` para representar as tabelas principais.
- A entity `PlaylistFile` representará a tabela `files`.
- A tabela `playlist_files` será representada por um relacionamento muitos-para-muitos entre `Playlist` e `PlaylistFile`, sem uma entity própria, pois não possui atributos além das duas chaves estrangeiras.
- Os nomes de tabelas e colunas serão declarados explicitamente para manter correspondência com as migrations.
- Os identificadores `id` serão gerados pelo banco com `IDENTITY`.
- Os campos obrigatórios e únicos serão representados conforme as restrições das migrations.
- O tipo do arquivo será representado por um enum Java persistido como texto.
- Lombok já está disponível no projeto e poderá ser usado para reduzir código repetitivo, sem adicionar dependências.
- Nenhuma migration ou versão de dependência será alterada.

## Etapas

1. Criar `tasks.md` com as tarefas derivadas deste plano.
2. Criar a entity `User` com os campos da tabela `users`.
3. Criar a entity `Playlist` com os campos da tabela `playlists` e o relacionamento com arquivos.
4. Criar a entity `PlaylistFile` com os campos da tabela `files` e o lado inverso do relacionamento com playlists.
5. Criar o enum que representa os valores aceitos por `files.type`.
6. Criar um teste global que valide o mapeamento das entities para suas tabelas e campos essenciais, sem repetir cada detalhe das migrations.
7. Executar os testes e empacotar a aplicação.
8. Atualizar o status e a branch da spec e registrar a implementação em um commit.

## Validação

- Executar `./mvnw test`.
- Executar `./mvnw package`.
- Confirmar que as entities compilam e são descobertas pelo contexto Spring.
- Confirmar a correspondência entre as tabelas principais e os campos declarados nas migrations.
- Confirmar que `playlist_files` é mapeada pelo relacionamento entre `Playlist` e `PlaylistFile`.
- Conferir o critério de aceite da spec.

## Critérios de conclusão

- As tabelas principais possuem entities correspondentes.
- Todos os campos das entities correspondem às migrations existentes.
- O relacionamento entre playlists e arquivos utiliza a tabela `playlist_files`.
- Os testes e o empacotamento terminam com sucesso.
- A spec e as tarefas registram a conclusão na branch `feature/004-criando-primeiras-entities`.
- A implementação está registrada em um commit.
