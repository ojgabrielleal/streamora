# Plano de implementação — Spec 021

## Objetivo

Expor os casos de uso de `FtpFileService` em uma API HTTP autenticada sob
`/storage`, com respostas e erros consistentes, contratos integralmente
documentados no OpenAPI e sem levar detalhes do sistema de arquivos para a
camada web.

## Decisões de implementação

- Criar `FtpFileController` com dependência exclusiva de `FtpFileService`; o
  controller apenas receberá os dados HTTP, delegará cada operação e montará
  as respostas `200 OK` ou `204 No Content`.
- Manter as sete rotas e métodos HTTP definidos pela spec, sem aliases ou
  operações adicionais.
- Criar records imutáveis específicos para criação de diretório, renomeação e
  movimentação, reutilizando o mesmo contrato de renomeação para arquivos e
  diretórios por possuir os mesmos campos `path` e `newName`.
- Preservar `targetDirectory` vazio no DTO e repassá-lo sem transformação ao
  service, mantendo a semântica existente de movimentação para a raiz.
- Criar `FtpFileEntryResponseDTO` como contrato recursivo da árvore retornada
  pela listagem e `FtpFileEntryMapper` para converter `FtpFileEntry`, mantendo
  o modelo de infraestrutura e suas dependências separados da camada HTTP.
- Tratar `FtpFileStorageException` na camada HTTP com um handler limitado ao
  controller, respondendo `400 Bad Request` com um corpo imutável que exponha
  somente a mensagem segura da operação, sem causa, stack trace ou caminho
  absoluto.
- Documentar tag, operações, query parameters, request bodies, schemas e as
  respostas aplicáveis (`200`, `204`, `400` e `401`) por anotações OpenAPI.
- Não adicionar lógica de autenticação nem tratamento de `401` ao controller.
  As rotas `/storage` permanecerão protegidas automaticamente pela regra atual
  `anyRequest().authenticated()` do Spring Security; o contrato `401` será
  apenas documentado e verificado por teste de integração.
- Organizar todos os DTOs da aplicação em subpacotes por domínio (`auth`,
  `ftp`, `playlists`, `schedules` e `users`), preservando os nomes das classes e
  os contratos JSON existentes.

## Etapas

1. Criar os DTOs imutáveis de criação de diretório, renomeação, movimentação,
   árvore de resposta e erro HTTP, com os nomes de campos definidos pelo
   domínio e metadados de schema OpenAPI.
2. Criar `FtpFileEntryMapper` para converter recursivamente a árvore interna em
   `FtpFileEntryResponseDTO`, com teste unitário próprio.
3. Criar `FtpFileController` com as sete rotas, delegação direta ao
   `FtpFileService`, status de sucesso explícitos e tradução local de
   `FtpFileStorageException` para `400 Bad Request`.
4. Criar testes do controller com `FtpFileService` mockado para validar rota
   base, delegação e argumentos, corpos e status HTTP e resposta segura de
   erro, sem implementar responsabilidades de autenticação no controller.
5. Atualizar os testes de segurança para comprovar que uma requisição anônima
   a `/storage` recebe `401 Unauthorized` pela configuração existente do
   Spring Security.
6. Atualizar `OpenApiDocumentationTests` para comprovar todos os paths,
   métodos, parâmetros, request bodies, schemas e respostas documentadas.
7. Criar `tasks.md` para acompanhar a execução e atualizar
   `docs/project-structure.md` com o controller, os DTOs e os novos testes.
8. Executar a suíte completa de testes e o empacotamento Maven, revisar os
   critérios de aceite e registrar qualquer limitação encontrada.
9. Reorganizar os DTOs em subpacotes por domínio, atualizar todos os imports e
   a documentação estrutural e reexecutar as validações sem alterar a API.

## Validação prevista

- Testes web isolados do controller comprovam `200` na listagem, `204` nas seis
  mutações e `400` nas falhas de storage.
- Um teste de integração com a configuração de segurança comprova o `401` sem
  autenticação, sem atribuir esse comportamento ao controller.
- Testes unitários comprovam a conversão recursiva da árvore e que cada
  endpoint chama somente o método e os argumentos correspondentes de
  `FtpFileService`.
- O documento `/v3/api-docs` contém as sete operações e os schemas completos
  de entrada, árvore e erro.
- Busca estrutural confirma que o controller não importa nem acessa
  `FtpFileStorage`, `Path`, `Files` ou o volume local.
- `./mvnw test` (ou `mvn test`) executa toda a suíte.
- `./mvnw verify -DskipTests` (ou equivalente) confirma compilação e
  empacotamento.

## Fora do escopo

- Alterar regras de validação ou manipulação física implementadas na Spec 020.
- Movimentar diretórios, excluir recursivamente ou sobrescrever destinos.
- Alterar dependências, versões, migrations ou configuração do volume FTP.
