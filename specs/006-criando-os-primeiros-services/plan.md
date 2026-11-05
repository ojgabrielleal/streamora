# Plano de implementação — Spec 006

## Objetivo

Criar services Spring para as entities existentes `User`, `Playlist` e
`PlaylistFile`, centralizando as operações de CRUD e o gerenciamento da relação
entre playlists e arquivos.

## Premissas

- Os services ficarão em `src/main/java/com/streamora/streamora/service`.
- Serão criadas as classes `UserService`, `PlaylistService` e
  `PlaylistFileService`, seguindo a convenção de nomes da comunidade Spring.
- Cada classe será anotada com `@Service` e receberá sua repository por injeção
  de dependência via construtor.
- As operações públicas usarão `uuid` para localizar registros, aproveitando o
  contrato `findByUuid` já presente nas repositories e evitando expor o `id`
  interno como identificador funcional.
- Cada service terá os métodos `index`, `show`, `store`, `update` e `delete`.
- `index` retornará todos os registros; `show` localizará um registro por
  `uuid`; `store` persistirá uma nova entity; `update` persistirá a entity
  recebida após validar a existência do `uuid`; e `delete` removerá o registro
  localizado pelo `uuid`.
- A ausência de um registro será representada por uma exceção própria e clara
  da camada de service, evitando retorno `null` e falhas genéricas.
- `PlaylistService` ficará responsável somente pelas operações de CRUD de
  playlists.
- `PlaylistFileService` terá operações para adicionar e remover o arquivo de
  uma playlist, centralizando essa responsabilidade em um único service.
- As operações de associação receberão os UUIDs da playlist e do arquivo,
  atualizarão os dois lados da relação em memória e salvarão `Playlist`, que é
  o lado proprietário do relacionamento JPA.
- As operações que alteram mais de um estado persistente serão transacionais.
- Nenhuma dependência, migration, entity ou repository será alterada.

## Etapas

1. Criar `tasks.md` com as tarefas derivadas deste plano.
2. Criar a exceção usada quando uma entity solicitada não existir.
3. Implementar `UserService` com as cinco operações de CRUD.
4. Implementar `PlaylistService` com as cinco operações de CRUD.
5. Implementar `PlaylistFileService` com CRUD e operações de adicionar/remover
   o arquivo de uma playlist.
6. Criar testes unitários dos services, isolando as repositories com mocks e
   cobrindo CRUD, ausência de registros e manutenção da associação.
7. Atualizar `docs/project-structure.md` com o novo pacote e a organização dos
   testes.
8. Executar os testes e empacotar a aplicação.
9. Conferir os critérios de aceite da spec e solicitar revisão/correções.
10. Após autorização de finalização, atualizar o status e a branch da spec e
    registrar a implementação em um commit.

## Validação

- Executar `./mvnw test`.
- Executar `./mvnw package`.
- Confirmar que os três services são componentes Spring e dependem somente das
  repositories necessárias.
- Confirmar que todos expõem `index`, `show`, `store`, `update` e `delete`.
- Confirmar que associações podem ser adicionadas e removidas por meio do
  `PlaylistFileService`.
- Confirmar que as associações mantêm os dois lados do modelo consistentes e
  persistem o lado proprietário.
- Confirmar o comportamento quando playlist, arquivo ou registro de CRUD não
  existe.
- Conferir o critério de aceite da spec.

## Critérios de conclusão

- Cada entity principal possui um service correspondente com CRUD completo.
- `PlaylistFileService` oferece as operações adicionais exigidas para gerenciar
  arquivos em playlists, sem duplicar essa responsabilidade em
  `PlaylistService`.
- Os testes unitários, a suíte existente e o empacotamento terminam com sucesso.
- A documentação da estrutura representa os novos componentes.
- Após revisão e autorização, a spec registra o status e a branch da execução,
  e a implementação fica registrada em um commit.
