# Plano de implementação — Spec 005

## Objetivo

Criar repositories Spring Data JPA para as entities existentes `User`, `Playlist` e `PlaylistFile`, incluindo busca por `uuid`.

## Premissas

- As repositories ficarão em `src/main/java/com/streamora/streamora/repository`.
- Serão criadas as interfaces `UserRepository`, `PlaylistRepository` e `PlaylistFileRepository`, seguindo a convenção do Spring Data.
- Cada interface estenderá `JpaRepository<Entity, Long>`.
- Cada repository declarará `Optional<Entity> findByUuid(String uuid)` para expressar a possibilidade de ausência do registro.
- As repositories serão baseadas nas entities existentes em `src/main/java/com/streamora/streamora/entity`.
- Nenhuma dependência, migration ou entity será alterada.
- Os testes validarão globalmente a estrutura comum das repositories, evitando repetição por interface.

## Etapas

1. Criar `tasks.md` com as tarefas derivadas deste plano.
2. Criar `UserRepository` baseada em `User`.
3. Criar `PlaylistRepository` baseada em `Playlist`.
4. Criar `PlaylistFileRepository` baseada em `PlaylistFile`.
5. Criar um teste global que confirme a existência das repositories, a extensão de `JpaRepository` e o acessor `findByUuid` com retorno `Optional`.
6. Executar os testes e empacotar a aplicação.
7. Conferir os critérios de aceite da spec.
8. Após a revisão e autorização de finalização, atualizar o status e a branch da spec e registrar a implementação em um commit.

## Validação

- Executar `./mvnw test`.
- Executar `./mvnw package`.
- Confirmar que as três interfaces compilam e são reconhecidas como repositories Spring Data.
- Confirmar que cada repository usa `Long` como tipo do identificador da entity correspondente.
- Confirmar que cada repository oferece `findByUuid(String uuid)` com retorno `Optional` da entity correspondente.
- Conferir o critério de aceite da spec.

## Critérios de conclusão

- Cada entity principal existente possui uma repository correspondente.
- Todas as repositories fornecem busca por `uuid`.
- Os testes e o empacotamento terminam com sucesso.
- A spec e as tarefas registram a conclusão na branch `feature/005-criando-primeiras-repositories`.
- A implementação está registrada em um commit autorizado.
