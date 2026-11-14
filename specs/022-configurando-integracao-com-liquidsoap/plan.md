# Plano de implementação — Spec 022

## Objetivo

Integrar a aplicação ao Liquidsoap por meio dos volumes locais já
compartilhados, gerando M3U seguros para as playlists e dois arquivos `.liq`
com fontes ponderadas e grade semanal, com retorno automático às playlists
base quando não houver agendamento reproduzível.

## Decisões de implementação

- Criar uma única migration de `playlists` com `is_base BOOLEAN NOT NULL
  DEFAULT FALSE` e `weight INT UNSIGNED NOT NULL DEFAULT 1`, preservando os
  registros existentes e garantindo no banco que o peso seja positivo por
  `CHECK`.
- Expor `isBase` e `weight` na entity, nos DTOs de request, response e resumo,
  no mapper, nas factories e nos testes dos contratos existentes. O service de
  playlists continuará delegando o mapeamento e a persistência, e o mapper
  rejeitará pesos nulos ou menores que um.
- Configurar por propriedades os diretórios usados pela integração, mantendo
  como padrões `/pure-ftpd/uploads`, `/liquidsoap/m3u` e
  `/liquidsoap/config`. Os testes substituirão esses valores por diretórios
  temporários.
- Representar os caminhos escritos nos M3U com a raiz visível igualmente pela
  aplicação e pelo Liquidsoap (`/pure-ftpd/uploads`). Todo caminho de entrada
  será normalizado, validado contra essa raiz e rejeitado se for absoluto fora
  dela, escapar por `..` ou atravessar link simbólico.
- Separar a integração em componentes pequenos dentro de
  `infrastructure/liquidsoap`: propriedades e validação de caminhos; geração de
  conteúdo M3U; escrita de arquivos M3U; seleção e combinação de playlists;
  geração dos conteúdos de fontes e grade; e escrita dos arquivos `.liq`.
- Usar records internos imutáveis para transportar uma fonte de playlist e
  sua combinação ponderada. A deduplicação será feita pela identidade pública
  da playlist antes da aplicação dos pesos, mantendo uma playlist uma única
  vez mesmo com agendamentos sobrepostos.
- Considerar reproduzível somente a playlist que tenha nome M3U simples e cujo
  arquivo regular exista, seja legível e possua ao menos uma entrada não vazia.
  A filtragem será individual para não descartar as demais fontes válidas.
- Gerar o arquivo de fontes com uma fonte segura por M3U e combinações
  ponderadas para os conjuntos base e agendados. Gerar a grade separadamente,
  com predicados semanais derivados de `dayOfWeek`, `startTime` e `endTime`,
  priorizando os conjuntos agendados reproduzíveis e usando a combinação base
  como fallback.
- Preservar a validação atual de horários, na qual `endTime` deve ser posterior
  a `startTime`; intervalos que atravessam a meia-noite permanecem fora do
  modelo atual e não serão introduzidos implicitamente nesta spec.
- Criar `LiquidsoapService` como coordenador dos services existentes, sem
  controller próprio nesta spec. `PlaylistService` fornecerá as operações do
  domínio de playlists, incluindo seleção das bases e persistência do nome do
  M3U; `ScheduleService` fornecerá os agendamentos e sua seleção temporal. O
  coordenador solicitará deduplicação, ponderação, geração e escrita dos
  arquivos sem consultar repositories diretamente.
- Escrever cada arquivo por substituição segura no diretório de destino e
  limitar os nomes aos arquivos esperados. Nenhuma operação manipulará o
  conteúdo das mídias.
- Ajustar `radio.liq` para carregar os dois arquivos gerados e manter apenas a
  saída Icecast e o fallback técnico já existente. Senhas e demais credenciais
  continuarão sendo lidas diretamente das variáveis de ambiente.
- Não criar endpoints HTTP, rotinas de inicialização, reinício ou monitoramento
  do Liquidsoap e não adicionar dependências.
- Registrar que mudanças no conteúdo de M3Us poderão ser percebidas pelo
  `reload` configurado no Liquidsoap, mas mudanças nos arquivos `.liq` somente
  entrarão em execução após reload ou reinício externo do processo, operação
  deliberadamente fora do escopo desta spec.

## Etapas

1. Criar a migration de `playlists` para `is_base` e `weight` e atualizar
   `docs/database-schema.md`.
2. Mapear os campos novos em `Playlist`, nos três DTOs de playlist, no mapper,
   nas factories e nos testes de entity, mapper, service, controller e OpenAPI.
3. Adicionar as propriedades da integração e o validador de caminhos do
   storage compartilhado, cobrindo normalização, escape e links simbólicos.
4. Implementar a geração de conteúdo M3U para uma mídia, lista de mídias e
   diretório, sem filtrar extensões, além da escrita no diretório configurado.
5. Implementar no `PlaylistService` a associação persistente do nome simples
   do M3U e a seleção das playlists base; manter a persistência posterior à
   escrita bem-sucedida.
6. Implementar no `ScheduleService` a disponibilização dos agendamentos e sua
   seleção por dia e horário; implementar na integração a verificação
   individual de disponibilidade, a deduplicação e a composição por pesos.
7. Implementar os geradores separados de fontes/combinações e de grade
   semanal, incluindo troca entre base e agendamentos, simultaneidade,
   sobreposição e fallback das fontes inválidas.
8. Implementar `LiquidsoapService` como coordenador sem controller e a escrita
   segura dos dois `.liq`; ajustar `liquidsoap/radio.liq` para referenciá-los.
9. Criar testes unitários da integração com `@TempDir`, repositories mockadas e
   relógio/momento controlado quando necessário, sem acessar volumes reais nem
   executar o Liquidsoap.
10. Criar `tasks.md`, atualizar `docs/project-structure.md`, executar toda a
    suíte e o empacotamento Maven e revisar cada critério de aceite.

## Validação prevista

- Testes de M3U comprovam geração a partir de arquivo, lista e diretório,
  mistura de extensões, caminhos compatíveis com o volume e rejeição de escape
  da raiz compartilhada.
- Testes de persistência comprovam que somente o nome do M3U é armazenado, que
  uma regeneração substitui seu conteúdo e que falhas de escrita não alteram a
  playlist.
- Testes de seleção comprovam bases múltiplas, schedules simultâneos,
  deduplicação de sobreposições e proporções preservadas por pesos positivos.
- Testes da grade comprovam ausência de agendamento, entrada e saída de um
  intervalo, retorno às bases e exclusão de schedules expirados ou futuros.
- Testes de tolerância a falhas comprovam descarte individual de M3U ausente,
  vazio ou ilegível, manutenção das demais fontes e fallback para bases quando
  nenhum agendamento ativo for reproduzível.
- Testes de escrita usam somente diretórios temporários e verificam que os
  conteúdos de fontes e grade são gravados em arquivos distintos.
- Uma inspeção dos `.liq` confirma que não existem credenciais literais e que
  `radio.liq` continua lendo a senha e o mount pelas variáveis de ambiente.
- `mvn test` executa a suíte completa e `mvn verify -DskipTests` confirma a
  compilação e o empacotamento.

## Fora do escopo

- Endpoints HTTP para gerar ou regenerar arquivos.
- Controle do processo, reload, reinício ou monitoramento do Liquidsoap.
- Cópia, conversão, edição, exclusão ou validação por extensão das mídias.
- Escolha e implementação de um arquivo de áudio para fallback final.
- Suporte novo a schedules que atravessam a meia-noite.
- Alteração de dependências, versões ou comunicação remota com o Liquidsoap.
