# Plano de implementação — Spec 024

## Objetivo

Carregar as faixas do M3U somente no detalhe de uma playlist e permitir sua
substituição integral por caminhos relativos do storage, reutilizando a
infraestrutura existente e preservando o M3U atual diante de qualquer entrada
inválida.

## Decisões de implementação

- Manter `GET /playlist` usando `PlaylistService#index` e
  `PlaylistResponseDTO`, sem acesso ao filesystem.
- Alterar somente `GET /playlist/{uuid}` para delegar ao fluxo coordenador do
  Liquidsoap e retornar `PlaylistDetailsResponseDTO`, composto pelos campos
  atuais da playlist e por `tracks`.
- Criar `PlaylistTracksRequestDTO` com `List<String> paths` para
  `PUT /playlist/{uuid}/tracks`; a lista será validada antes de qualquer
  escrita e não poderá ser nula, vazia ou conter itens nulos ou em branco.
- Criar `M3uContentReader` em `infrastructure/liquidsoap`. O componente
  validará o nome simples do M3U, abrirá o arquivo em UTF-8, exigirá o cabeçalho
  `#EXTM3U`, ignorará comentários e linhas vazias e validará cada entrada como
  arquivo de mídia permitido.
- Converter cada caminho interno validado por `M3uContentReader` em caminho
  relativo à raiz configurada de mídia. A ordem e as ocorrências duplicadas
  serão preservadas.
- Considerar malformado um M3U sem cabeçalho, sem entrada reproduzível ou com
  qualquer entrada inválida. A leitura falhará por inteiro, sem devolver uma
  lista parcial.
- Criar uma exceção específica de leitura de M3U para diferenciar o estado
  inconsistente do detalhe (`409 Conflict`) dos erros de entrada do `PUT`
  (`400 Bad Request`).
- Quando `m3uFileName` for nulo, o detalhe retornará `tracks` vazio sem tentar
  acessar o diretório de M3Us.
- Criar um método específico no `LiquidsoapPathValidator` para caminhos
  relativos de mídia. O novo endpoint rejeitará caminhos absolutos mesmo que
  eles apontem para dentro do storage; os fluxos internos já existentes
  continuarão aceitando suas entradas atuais.
- Coordenar consulta e substituição em `LiquidsoapService`, que já concentra a
  relação entre playlist, M3U e componentes da infraestrutura. Não mover o
  gerador ou o writer para `PlaylistService`.
- Na substituição, localizar primeiro a playlist para garantir `404` sem criar
  arquivo, gerar o conteúdo completo em memória, escrever atomicamente
  `{playlistUuid}.m3u` e somente então persistir a associação.
- Retornar `PlaylistDetailsResponseDTO` usando as faixas já validadas da
  requisição depois da escrita bem-sucedida, sem reabrir desnecessariamente o
  M3U na mesma operação.
- Adicionar ao `PlaylistMapper` uma conversão específica para o DTO detalhado,
  reutilizando a mesma conversão de horários do DTO atual.
- Criar `PlaylistErrorDTO` para mensagens HTTP seguras e tratar no
  `PlaylistController`: recurso inexistente como `404`, entrada ou escrita
  inválida como `400` e falha de leitura do M3U associado como `409`.
- Não chamar `applyConfiguration` nem o controle do processo Liquidsoap em
  nenhum ponto do novo fluxo.

## Fluxo de consulta detalhada

1. `PlaylistController#show` delega o UUID ao `LiquidsoapService`.
2. O service busca a playlist existente por `PlaylistService`.
3. Se não houver M3U associado, o service monta o detalhe com `tracks` vazio.
4. Se houver associação, `M3uContentReader` valida e lê o arquivo completo.
5. O reader devolve somente caminhos relativos de mídia, preservando ordem e
   ocorrências.
6. O mapper monta `PlaylistDetailsResponseDTO` e o controller retorna `200`.

## Fluxo de substituição

1. `PlaylistController` recebe UUID e `PlaylistTracksRequestDTO`.
2. `LiquidsoapService` confirma primeiro que a playlist existe.
3. O gerador valida todos os caminhos como relativos, existentes, legíveis,
   regulares, não simbólicos e internos ao storage.
4. Somente depois da validação completa, o writer substitui atomicamente
   `{playlistUuid}.m3u`.
5. `PlaylistService` associa o nome simples à playlist e persiste a alteração.
6. O mapper devolve o detalhe com as faixas enviadas e o controller retorna
   `200 OK`.

## Etapas

1. Criar `PlaylistTracksRequestDTO`, `PlaylistDetailsResponseDTO` e
   `PlaylistErrorDTO` conforme os padrões de records e OpenAPI.
2. Ampliar `LiquidsoapPathValidator` com validação exclusiva de caminho
   relativo e cobrir o novo contrato por testes.
3. Implementar `M3uContentReader` com validação segura de arquivo, conteúdo e
   conversão para caminhos relativos.
4. Ampliar `PlaylistMapper` com a construção do detalhe sem duplicar o
   mapeamento de schedules.
5. Implementar em `LiquidsoapService` a consulta detalhada e a substituição de
   faixas, garantindo a ordem playlist → validação → escrita → associação.
6. Alterar `PlaylistController#show`, criar `PUT /playlist/{uuid}/tracks` e
   adicionar os tratamentos seguros de `400`, `404` e `409`.
7. Criar testes unitários do reader, mapper e service para todos os casos de
   sucesso e falha definidos na spec.
8. Atualizar os testes do controller, segurança e OpenAPI para os novos
   contratos e schemas.
9. Criar `tasks.md`, atualizar `docs/project-structure.md` e executar a suíte,
   o empacotamento e a revisão dos critérios de aceite.

## Estratégia de testes

- Testes do reader usarão `@TempDir` e arquivos reais temporários para cobrir
  cabeçalho, comentários, linhas vazias, duplicidades, ordem e caminhos
  relativos retornados.
- Casos de M3U ausente, ilegível, vazio, sem cabeçalho, sem faixa ou com
  entrada inválida comprovarão a falha integral de leitura.
- Testes do service comprovarão detalhe sem M3U, detalhe com faixas, busca da
  playlist antes da escrita, nome automático, escrita antes da associação e
  ausência de controle do processo Liquidsoap.
- Falhas para lista nula, vazia, item nulo, item em branco, caminho absoluto,
  inexistente, diretório, link simbólico ou escape da raiz comprovarão que o
  writer e a associação não são chamados.
- Testes web comprovarão o DTO detalhado em `GET /playlist/{uuid}`, a listagem
  atual sem `tracks`, o contrato do `PUT` e os status `400`, `404` e `409`.
- Testes OpenAPI comprovarão request body, schemas e respostas documentadas.
- `./mvnw test` executará a suíte completa e `./mvnw verify -DskipTests`
  confirmará compilação e empacotamento.

## Fora do escopo

- Endpoints incrementais para adicionar ou remover uma faixa.
- Seleção ou persistência da origem por diretório.
- Comentários próprios do Streamora dentro do M3U.
- Remoção de M3Us antigos ou órfãos.
- Reinício ou aplicação automática do Liquidsoap.
- Migration, dependência nova ou alteração de versões.
