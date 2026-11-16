# Spec 024 - Gerenciando faixas da playlist

**Status:** Concluída

## Objetivo

Incluir as faixas atuais na consulta detalhada de uma playlist e disponibilizar
uma operação HTTP autenticada para substituí-las a partir dos caminhos
existentes no storage compartilhado, permitindo que a interface carregue o
conteúdo atual antes de gerar ou atualizar automaticamente seu arquivo M3U.

## Requisitos

### Contrato HTTP

- Ampliar o `GET /playlist/{uuid}` existente para retornar também as faixas
  registradas no M3U associado à playlist.
- Manter `GET /playlist` como listagem leve, sem abrir os arquivos M3U nem
  carregar suas faixas.
- Expor `PUT /playlist/{uuid}/tracks` para definir integralmente as faixas da
  playlist identificada pelo UUID, tanto na configuração inicial quanto nas
  atualizações posteriores.
- Retornar no detalhe da playlist o campo `tracks`, contendo os caminhos
  relativos das mídias na ordem em que aparecem no M3U.
- Receber um corpo JSON imutável com o campo obrigatório `paths`, contendo a
  lista de caminhos das mídias no storage compartilhado.
- Usar caminhos relativos à raiz do storage, com a mesma representação exposta
  pelos endpoints de `/storage`.
- Retornar HTTP `200 OK` com a playlist atualizada quando o M3U for escrito e
  associado com sucesso.
- Retornar HTTP `200 OK` com `tracks` vazio no detalhe quando a playlist
  existir, mas ainda não possuir um M3U associado.
- Retornar HTTP `400 Bad Request` quando `paths` for nulo, estiver vazio,
  contiver caminho nulo ou em branco ou possuir um caminho inválido.
- Retornar HTTP `404 Not Found` quando o UUID não identificar uma playlist.
- Retornar HTTP `409 Conflict` no `GET` quando a playlist possuir um nome M3U
  associado, mas o arquivo estiver ausente, ilegível ou malformado.
- Manter o endpoint protegido pelas regras atuais do Spring Security.

### Geração e associação do M3U

- Gerar automaticamente o nome do arquivo M3U no formato
  `{playlistUuid}.m3u`.
- Criar e associar esse M3U quando a playlist ainda não possuir um arquivo;
  quando já possuir, substituir integralmente seu conteúdo.
- Não receber `m3uFileName` no contrato do novo endpoint.
- Reutilizar o fluxo existente de `LiquidsoapService` para gerar o M3U a
  partir de uma lista de caminhos, escrever o arquivo e associá-lo à playlist.
- Manter `M3uContentGenerator`, `LiquidsoapFileWriter` e os demais componentes
  técnicos em `infrastructure/liquidsoap`; a nova rota no recurso de playlist
  não deve mover nem duplicar essas responsabilidades.
- Substituir integralmente o conteúdo do M3U a cada chamada bem-sucedida.
- Preservar a ordem e as ocorrências dos caminhos recebidos ao escrever o M3U.
- Validar todos os caminhos antes de substituir o arquivo existente.
- Não alterar a associação nem o conteúdo atual do M3U se qualquer caminho da
  requisição for inválido.
- Persistir em `playlists.m3u_file_name` somente o nome gerado, sem caminho de
  diretório.

### Leitura do M3U

- Criar um componente de leitura em `infrastructure/liquidsoap` para abrir o
  M3U associado e extrair suas entradas de mídia.
- Ignorar o cabeçalho `#EXTM3U`, comentários, metadados e linhas vazias ao
  montar a resposta.
- Converter os caminhos absolutos internos do M3U para caminhos relativos à
  raiz do storage antes de retorná-los pela API.
- Preservar a ordem e as ocorrências das entradas encontradas no M3U.
- Validar cada entrada lida com as mesmas regras de permanência dentro da raiz
  compartilhada, existência, legibilidade e rejeição de links simbólicos.
- Nunca expor na resposta os caminhos absolutos usados internamente pelos
  containers.
- Não modificar o arquivo M3U durante uma consulta.

### Relação com o Liquidsoap

- Não chamar automaticamente `POST /liquidsoap/apply` depois da atualização
  das faixas.
- Manter o processo do Liquidsoap em execução, pois o conteúdo de um M3U já
  conhecido é recarregado pelo intervalo configurado na fonte.
- Quando a playlist ainda não fizer parte da configuração em execução, sua
  criação estrutural continuará dependendo de uma chamada explícita a
  `POST /liquidsoap/apply`.

### Validação e erros

- Rejeitar uma lista vazia para impedir a criação de uma playlist não
  reproduzível.
- Reutilizar as regras atuais de normalização, permanência dentro da raiz
  permitida e rejeição de links simbólicos.
- Rejeitar mídias inexistentes, ilegíveis ou fora do storage compartilhado.
- Retornar mensagens seguras sem expor caminhos absolutos internos, stack
  traces ou detalhes do sistema de arquivos.
- Não copiar, converter, editar ou excluir os arquivos de mídia.

### Testes e documentação

- Criar `PlaylistTracksRequestDTO` com `paths` para o corpo do `PUT`.
- Criar `PlaylistDetailsResponseDTO` com os dados atuais de
  `PlaylistResponseDTO` e o campo adicional `tracks`, usando-o somente em
  `GET /playlist/{uuid}` e na resposta do `PUT /playlist/{uuid}/tracks`.
- Manter `PlaylistResponseDTO` no `GET /playlist`, evitando leitura de todos os
  M3Us durante a listagem.
- Documentar no Swagger/OpenAPI o contrato detalhado do `GET` existente e a
  nova operação `PUT`, incluindo path parameter, request body, schemas e as
  respostas `200`, `400`, `401`, `404` e `409` aplicáveis.
- Criar testes do controller com as dependências substituídas por mocks.
- Criar testes da leitura de M3U para cabeçalho, comentários, linhas vazias,
  ordem, ocorrências duplicadas e conversão para caminhos relativos.
- Criar testes do fluxo coordenador para nome automático, escrita antes da
  associação e preservação do estado quando a validação falhar.
- Criar testes para lista nula, vazia, item nulo, item em branco e caminho
  inválido.
- Atualizar `docs/project-structure.md` com o DTO e os testes adicionados.

## Contrato proposto

### Consultar playlist com suas faixas

```http
GET /playlist/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer {accessToken}
```

```json
{
  "uuid": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Programação da manhã",
  "m3uFileName": "550e8400-e29b-41d4-a716-446655440000.m3u",
  "isBase": true,
  "weight": 1,
  "schedules": [],
  "tracks": [
    "musicas/artista/faixa-1.mp3",
    "musicas/artista/faixa-2.flac"
  ]
}
```

### Definir faixas inicialmente ou atualizá-las

```http
PUT /playlist/550e8400-e29b-41d4-a716-446655440000/tracks
Content-Type: application/json
Authorization: Bearer {accessToken}
```

```json
{
  "paths": [
    "musicas/artista/faixa-1.mp3",
    "musicas/artista/faixa-2.flac"
  ]
}
```

### Resposta de sucesso

```http
HTTP/1.1 200 OK
```

A operação cria e associa o M3U na primeira definição de faixas. Nas chamadas
posteriores, substitui integralmente o conteúdo existente. A resposta deve usar
`PlaylistDetailsResponseDTO`, informar o nome automaticamente associado e
devolver as faixas gravadas:

```json
{
  "uuid": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Programação da manhã",
  "m3uFileName": "550e8400-e29b-41d4-a716-446655440000.m3u",
  "isBase": true,
  "weight": 1,
  "schedules": [],
  "tracks": [
    "musicas/artista/faixa-1.mp3",
    "musicas/artista/faixa-2.flac"
  ]
}
```

## Regras

- O endpoint representa a alteração das faixas da playlist e, por isso, deve
  permanecer em `PlaylistController`.
- `PlaylistController` deve apenas validar o contrato HTTP, delegar a operação
  e construir a resposta.
- O formato M3U e sua escrita continuam sendo detalhes da integração com o
  Liquidsoap.
- A operação usa semântica de substituição completa; não serão criadas rotas
  separadas para adicionar ou remover uma única faixa nesta spec.
- A interface deve carregar `GET /playlist/{uuid}`, alterar localmente somente
  as faixas desejadas e enviar a lista resultante pelo `PUT`; o usuário não
  precisará selecionar novamente todas as mídias já associadas.
- A leitura do M3U deve ocorrer somente no detalhe de uma playlist, nunca na
  listagem geral.
- A validação completa deve acontecer antes da escrita para que uma requisição
  inválida não inutilize um M3U válido.
- Nenhuma migration ou dependência nova deve ser adicionada.
- Todo código Java criado ou alterado deve seguir `docs/code-standards.md`.

## Fora do escopo

- Upload de arquivos de mídia pelo endpoint de playlists.
- Inclusão ou remoção incremental de uma única faixa.
- Ordenação manual persistida fora do próprio M3U.
- Remoção física de M3Us antigos ou não associados.
- Aplicação automática da configuração estrutural do Liquidsoap.
- Alteração do contrato CRUD atual de playlists.

## Critérios de aceite

- [x] `GET /playlist/{uuid}` retorna `PlaylistDetailsResponseDTO` com os
  caminhos relativos do M3U em `tracks`, preservando ordem e ocorrências.
- [x] `GET /playlist` continua usando `PlaylistResponseDTO` sem ler os M3Us.
- [x] O detalhe e a definição de faixas estão autenticados e documentados no
  OpenAPI.
- [x] O endpoint recebe uma lista não vazia de caminhos relativos do storage.
- [x] O M3U usa automaticamente o nome `{playlistUuid}.m3u`.
- [x] Uma chamada válida cria e associa o M3U quando necessário ou substitui
  integralmente suas faixas, retornando a playlist atualizada.
- [x] Lista nula, vazia ou com itens nulos ou em branco retorna `400 Bad
  Request`.
- [x] Caminhos inexistentes, ilegíveis, simbólicos ou fora do storage retornam
  `400 Bad Request`.
- [x] Uma requisição inválida preserva o conteúdo e a associação existentes.
- [x] Playlist inexistente retorna `404 Not Found` sem criar arquivo M3U.
- [x] Playlist sem M3U associado retorna `200 OK` com `tracks` vazio.
- [x] M3U associado ausente, ilegível ou malformado retorna `409 Conflict` com
  mensagem segura.
- [x] A consulta nunca expõe caminhos absolutos internos nem modifica o M3U.
- [x] O nome simples do M3U é persistido somente depois da escrita
  bem-sucedida.
- [x] A atualização das faixas não reinicia nem reaplica automaticamente o
  Liquidsoap.
- [x] A geração e escrita continuam nos componentes da infraestrutura do
  Liquidsoap, sem duplicação no controller ou no service de playlists.
- [x] Os testes não acessam os volumes reais nem executam o Liquidsoap.
- [x] `docs/project-structure.md` descreve os componentes adicionados.
- [x] O projeto compila e todos os testes passam.
