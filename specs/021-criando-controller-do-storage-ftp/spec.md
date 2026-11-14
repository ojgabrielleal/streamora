# Spec 021 - Criando controller do storage FTP

**Status:** Concluída

## Objetivo

Disponibilizar uma API HTTP autenticada para consultar e manipular os arquivos
e diretórios do storage compartilhado com o Pure-FTPd, reutilizando os casos de
uso existentes em `FtpFileService` e documentando integralmente os contratos no
Swagger/OpenAPI.

## Requisitos

- Criar `FtpFileController` no pacote `controller` com a rota-base `/storage`.
- O controller deve depender somente de `FtpFileService` e não deve acessar
  diretamente `FtpFileStorage`, `Path`, `Files` ou o volume local.
- Expor `GET /storage` para listar recursivamente a árvore do storage.
- Expor `DELETE /storage/files` com o parâmetro de query `path` para excluir um
  arquivo.
- Expor `PUT /storage/files/rename` para renomear um arquivo.
- Expor `PUT /storage/files/move` para mover um arquivo para um diretório
  existente ou para a raiz permitida.
- Expor `POST /storage/directories` para criar um diretório.
- Expor `PUT /storage/directories/rename` para renomear um diretório.
- Expor `DELETE /storage/directories` com o parâmetro de query `path` para
  excluir um diretório vazio.
- Criar DTOs imutáveis para os corpos das operações de criação, renomeação e
  movimentação.
- Os DTOs devem usar nomes de campos alinhados ao domínio:
  `path`, `newName` e `targetDirectory`.
- Criar um DTO de resposta próprio para a árvore retornada pela listagem, com
  `name`, `type` e `children`, convertendo recursivamente `FtpFileEntry` por um
  mapper e mantendo o modelo de infraestrutura fora do contrato HTTP.
- Retornar HTTP `200 OK` na listagem.
- Retornar HTTP `204 No Content` nas operações de criação, exclusão,
  renomeação e movimentação concluídas com sucesso.
- Traduzir `FtpFileStorageException` para HTTP `400 Bad Request`, preservando
  uma mensagem segura que descreva a falha da operação.
- Manter todos os endpoints protegidos pelas regras atuais do Spring Security.
- Documentar no Swagger/OpenAPI o controller, cada operação, parâmetros,
  request bodies, schemas e respostas `200`, `204`, `400` e `401` aplicáveis.
- Criar testes do controller com `FtpFileService` substituído por mock.
- Atualizar os testes de documentação OpenAPI para comprovar a exposição dos
  novos endpoints e schemas.
- Atualizar `docs/project-structure.md` com os novos componentes e testes.

## Contratos HTTP

### Listar o storage

- Método e rota: `GET /storage`.
- Resposta de sucesso: `200 OK` com uma lista de `FtpFileEntry`.

### Excluir arquivo

- Método e rota: `DELETE /storage/files?path={path}`.
- Resposta de sucesso: `204 No Content`.

### Renomear arquivo

- Método e rota: `PUT /storage/files/rename`.
- Corpo: `path` e `newName`.
- Resposta de sucesso: `204 No Content`.

### Mover arquivo

- Método e rota: `PUT /storage/files/move`.
- Corpo: `path` e `targetDirectory`.
- `targetDirectory` vazio deve representar a raiz permitida, conforme o
  contrato já existente do service.
- Resposta de sucesso: `204 No Content`.

### Criar diretório

- Método e rota: `POST /storage/directories`.
- Corpo: `path`.
- Resposta de sucesso: `204 No Content`.

### Renomear diretório

- Método e rota: `PUT /storage/directories/rename`.
- Corpo: `path` e `newName`.
- Resposta de sucesso: `204 No Content`.

### Excluir diretório

- Método e rota: `DELETE /storage/directories?path={path}`.
- A operação deve continuar limitada a diretórios vazios.
- Resposta de sucesso: `204 No Content`.

## Regras

- O controller deve apenas validar o contrato HTTP, delegar ao service e
  construir a resposta HTTP.
- As regras de segurança de caminhos e manipulação física continuam sob
  responsabilidade da implementação criada na Spec 020.
- Caminhos devem permanecer relativos à raiz configurada.
- A API não pode expor caminhos absolutos do servidor, stack traces ou detalhes
  internos do sistema de arquivos.
- Nenhuma operação pode introduzir movimentação de diretórios, exclusão
  recursiva ou sobrescrita de destinos existentes.
- Não devem ser adicionadas dependências nem alteradas versões existentes.
- As convenções de `docs/code-standards.md` devem ser seguidas em todo código
  Java criado ou alterado.

## Critérios de aceite

- [x] `GET /storage` retorna a árvore fornecida por `FtpFileService`.
- [x] Arquivos podem ser excluídos, renomeados e movidos pelos endpoints
  definidos.
- [x] Diretórios podem ser criados, renomeados e excluídos quando vazios pelos
  endpoints definidos.
- [x] As operações sem corpo de resposta retornam `204 No Content`.
- [x] Falhas de validação ou manipulação do storage retornam `400 Bad Request`
  sem expor detalhes internos.
- [x] Requisições anônimas aos endpoints de `/storage` retornam `401
  Unauthorized`.
- [x] Todos os endpoints, parâmetros, request bodies, schemas e respostas estão
  disponíveis no Swagger/OpenAPI.
- [x] O controller depende somente de `FtpFileService`.
- [x] Os testes do controller comprovam a delegação e os status HTTP.
- [x] Os testes de documentação comprovam a presença dos novos contratos.
- [x] `docs/project-structure.md` descreve os novos componentes.
- [x] O projeto compila e todos os testes passam.
