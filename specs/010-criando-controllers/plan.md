# Plano de implementação — Spec 010

## Objetivo

Criar controllers HTTP para `User`, `Playlist` e `PlaylistFile`, expondo os
métodos já existentes nos services sem adicionar tratamento, conversão ou
regra de negócio à camada web.

## Contratos propostos

- `UserController`, sob o prefixo `/user`:
  - `GET /user` chama `UserService.index()`;
  - `GET /user/{uuid}` chama `UserService.show(uuid)`;
  - `POST /user` chama `UserService.store(request)`;
  - `PUT /user/{uuid}` chama `UserService.update(uuid, request)`;
  - `DELETE /user/{uuid}` chama `UserService.delete(uuid)`.
- `PlaylistController`, sob o prefixo `/playlist`, com o mesmo mapeamento CRUD
  para `PlaylistService`.
- `PlaylistFileController`, sob o prefixo `/playlist-file`, com o mesmo
  mapeamento CRUD para `PlaylistFileService`, além de:
  - `POST /playlist-file/{fileUuid}/playlist/{playlistUuid}` chama
    `addToPlaylist(fileUuid, playlistUuid)`;
  - `DELETE /playlist-file/{fileUuid}/playlist/{playlistUuid}` chama
    `removeFromPlaylist(fileUuid, playlistUuid)`.

Os controllers receberão seus services por injeção de construtor gerada pelo
Lombok com `@RequiredArgsConstructor`, seguindo o padrão dos services existentes
e sem adicionar dependências. Os corpos das requisições serão encaminhados como
os DTOs existentes, e as respostas dos services serão devolvidas diretamente.
O endpoint de exclusão apenas chamará o service e não retornará conteýo.

## Etapas

1. Criar `tasks.md` com as tarefas derivadas deste plano.
2. Criar os três controllers no pacote `controller`, usando as anotações do
   Spring MVC, `@RequiredArgsConstructor` para injeção de dependência e sem
   lógica adicional.
3. Criar testes unitários dos controllers, com services mockados, verificando os
   mapeamentos HTTP, os argumentos encaminhados, os retornos e a delegação
   exata de cada endpoint.
4. Atualizar `docs/project-structure.md` com o pacote de controllers e seus
   testes.
5. Executar a suíte de testes e empacotar a aplicação.
6. Conferir os critérios de aceite e solicitar revisão ou correções.
7. Apó autorização para finalizar, atualizar na spec o status e a branch e
   criar o commit da implementação.

## Fora do escopo

- Alterar services, DTOs, mappers, entities, repositories ou migrations.
- Adicionar validação, tratamento global de exceções ou regras de negócio.
- Adicionar dependências ou alterar versões.
- Configurar autenticação ou autorização dos endpoints.

## Validação

- Executar `./mvnw test` ou `mvn test`, conforme o executável disponível.
- Executar `./mvnw package` ou `mvn package`.
- Confirmar que cada método público dos controllers somente delega ao método
  correspondente do service.
- Confirmar os prefixos, verbos, caminhos, corpos e parâmetros dos endpoints.
- Confirmar que nenhum controller manipula ou converte dados.
