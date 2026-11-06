# Plano de implementação - Spec 008

## Objetivo

Centralizar nas entities a geração dos UUIDs de novos registros, removendo essa
responsabilidade dos services e mantendo os mappers sem atribuição de UUID.

## Abordagem proposta

- Adicionar a `User`, `Playlist` e `PlaylistFile` um método privado anotado com
  `@PrePersist` que atribua um UUID aleatório no formato textual esperado pela
  coluna de 36 caracteres.
- Usar `UUID.randomUUID().toString()` dentro de cada callback de persistência,
  de modo que a geração aconteça imediatamente antes da inserção pelo JPA.
- Remover dos métodos `store` dos três services a geração manual do UUID e os
  imports que deixarem de ser necessários.
- Revisar os três mappers para confirmar que seus métodos `toEntity` continuam
  sem chamar `setUuid`.

## Testes

1. Ampliar os testes de entities para verificar que cada entity declara um
   callback privado anotado com `@PrePersist`.
2. Invocar o callback nos testes e validar que ele gera um texto não vazio, no
   formato de UUID e compatível com o limite de 36 caracteres.
3. Adaptar os testes dos services para simular o comportamento de persistência
   das repositories, comprovando que os services não precisam atribuir o UUID
   para retornar as respostas após o `save`.
4. Manter os testes dos mappers garantindo que novas entities não recebem UUID
   durante o mapeamento.

## Arquivos previstos

- `src/main/java/com/streamora/streamora/entity/User.java`
- `src/main/java/com/streamora/streamora/entity/Playlist.java`
- `src/main/java/com/streamora/streamora/entity/PlaylistFile.java`
- `src/main/java/com/streamora/streamora/service/UserService.java`
- `src/main/java/com/streamora/streamora/service/PlaylistService.java`
- `src/main/java/com/streamora/streamora/service/PlaylistFileService.java`
- testes de entities, mappers e services afetados
- `specs/008-adicionando-geracao-de-uuid-automaticamente-nas-entities/tasks.md`

## Fora do escopo

- Alterar migrations ou a estrutura do banco de dados.
- Alterar DTOs, repositories ou contratos públicos dos services.
- Adicionar dependências.
- Executar mudanças previstas por outras specs.

## Validação

- Confirmar que todas as entities geram UUID no callback `@PrePersist`.
- Confirmar que nenhum mapper ou service atribui UUID com `setUuid`.
- Executar a suíte completa de testes.
- Executar a compilação Maven.
