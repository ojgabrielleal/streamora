# Spec 022 - Configurando integração com Liquidsoap

**Status:** Planejada

## Objetivo

Criar a integração Java com o Liquidsoap para gerar playlists M3U, transformar
as playlists base e os agendamentos persistidos em configurações reproduzíveis,
combinar playlists simultâneas por peso e manter o fluxo de áudio usando as
playlists base quando não houver uma programação válida em execução.

## Requisitos

### Estrutura da integração

- Criar os componentes Java da integração em
  `src/main/java/com/streamora/streamora/infrastructure/liquidsoap`.
- Disponibilizar métodos distintos para cada operação da integração, sem
  concentrar todo o fluxo de geração em um único método.
- Manter separadas, no mínimo, as operações de:
  - gerar um M3U a partir de uma mídia;
  - gerar um M3U a partir de uma lista de mídias;
  - gerar um M3U a partir de um diretório;
  - associar e persistir o nome do M3U em uma playlist;
  - selecionar as playlists base;
  - selecionar as playlists dos agendamentos ativos;
  - eliminar playlists duplicadas da combinação;
  - aplicar os pesos às playlists selecionadas;
  - gerar o arquivo `.liq` da grade de programação;
  - gerar o arquivo `.liq` das fontes e combinações ponderadas;
  - escrever cada arquivo gerado no diretório correspondente.
- Permitir que um serviço coordenador componha essas operações, mantendo as
  regras e os efeitos de cada etapa em métodos específicos e testáveis.
- Separar a geração e a escrita física de arquivos da coordenação com as
  entities, repositories e services da aplicação.
- Usar os volumes já compartilhados entre a aplicação e o container do
  Liquidsoap, sem criar uma comunicação remota para gerar os arquivos.

### Geração de arquivos M3U

- Disponibilizar operações para gerar um arquivo `.m3u` a partir de:
  - um único caminho de mídia;
  - uma lista de caminhos de mídia;
  - todos os arquivos de mídia contidos em um diretório informado.
- Permitir que um mesmo arquivo `.m3u` contenha caminhos para mídias de
  formatos diferentes.
- Não copiar, converter, editar ou excluir os arquivos de áudio durante a
  geração do M3U.
- Escrever os arquivos `.m3u` no diretório `/liquidsoap/m3u`, correspondente ao
  volume Docker `liquidsoap_m3u`.
- Associar o M3U gerado à playlist correspondente, persistindo em
  `playlists.m3u_file_name` somente o nome do arquivo, sem o caminho do
  diretório.
- Permitir a regeneração do M3U associado quando o conteúdo da playlist for
  alterado.
- Produzir arquivos compatíveis com o formato M3U e com caminhos que possam ser
  lidos pelo container do Liquidsoap.

### Playlists base e pesos

- Adicionar à tabela `playlists` a coluna booleana `is_base`, que identifica as
  playlists usadas quando não houver agendamento ativo.
- Permitir que várias playlists sejam marcadas como base ao mesmo tempo.
- Adicionar à tabela `playlists` a coluna `weight`, com valor inteiro positivo,
  para definir a proporção de participação da playlist em uma combinação.
- Mapear `is_base` e `weight` na entity `Playlist` e nos DTOs, mapper, service e
  contratos existentes de playlists.
- Criar uma migration exclusiva da tabela `playlists` para adicionar os novos
  campos.
- Atualizar `docs/database-schema.md` para documentar os campos adicionados.

### Geração da grade de programação

- Consultar os registros de `schedules` e as playlists relacionadas para gerar
  a configuração semanal de programação do Liquidsoap.
- Usar o arquivo informado por `Playlist.m3uFileName` como fonte de cada
  playlist base ou agendada.
- Considerar ativo somente o agendamento cujo dia da semana e intervalo de
  horário incluam o momento da reprodução.
- Quando não houver nenhum agendamento ativo, reproduzir em conjunto todas as
  playlists base disponíveis, respeitando seus pesos.
- Quando houver pelo menos um agendamento ativo, interromper a participação de
  todas as playlists base somente durante o intervalo agendado e reproduzir
  apenas as playlists dos agendamentos ativos.
- Ao terminar o último agendamento ativo, retornar automaticamente à combinação
  das playlists base.
- Permitir que duas ou mais playlists tenham agendamentos simultâneos e
  combiná-las conforme seus pesos.
- Incluir uma playlist somente uma vez na combinação quando ela possuir
  agendamentos sobrepostos no mesmo instante.

### Arquivos de configuração do Liquidsoap

- Gerar um arquivo `.liq` separado com a grade construída a partir dos
  agendamentos.
- Gerar outro arquivo `.liq` separado com as fontes e combinações ponderadas
  das playlists.
- Escrever os arquivos gerados no diretório de configuração compartilhado que
  a aplicação acessa por `/liquidsoap/config` e o Liquidsoap acessa por
  `/etc/liquidsoap`.
- Ajustar o `radio.liq` principal para referenciar os arquivos `.liq` gerados.
- Manter as credenciais do Icecast provenientes das variáveis de ambiente, sem
  gravar dados sensíveis nos arquivos gerados.

### Fallback e tolerância a falhas

- Ignorar uma playlist agendada cujo M3U esteja ausente, vazio, ilegível ou não
  possa ser carregado.
- Manter as demais playlists agendadas válidas em reprodução quando somente
  uma parte da combinação falhar.
- Usar as playlists base válidas como fallback quando nenhum dos agendamentos
  ativos puder ser reproduzido.
- Ignorar individualmente uma playlist base indisponível e manter as demais
  playlists base válidas em reprodução.
- Não reativar um agendamento anterior depois que seu intervalo tiver
  terminado.
- Preparar a configuração para receber futuramente um arquivo de áudio como
  fallback final, sem implementar ou escolher esse arquivo nesta spec.

### Testes e documentação

- Criar testes unitários para a geração de M3U a partir de um caminho, de uma
  lista e de um diretório.
- Criar testes para a persistência do nome do M3U na playlist.
- Criar testes para a geração da grade, incluindo ausência de agendamentos,
  mudança entre base e agendamento, horários simultâneos e sobrepostos.
- Criar testes para a combinação por peso e para a eliminação de playlists
  duplicadas na mesma combinação.
- Criar testes para os fallbacks entre playlists agendadas e playlists base.
- Executar os testes de escrita de arquivos em diretórios temporários, sem
  manipular os volumes reais do ambiente.
- Atualizar `docs/project-structure.md` com os componentes e testes da
  integração com o Liquidsoap.

## Regras

- Os componentes da integração devem manipular somente arquivos `.m3u` e
  `.liq`; os arquivos de áudio apenas serão referenciados pelos caminhos
  escritos nos M3U.
- Cada método deve possuir uma responsabilidade clara; um método de geração de
  conteúdo não deve também consultar o banco, persistir uma playlist e escrever
  diferentes arquivos físicos.
- Métodos coordenadores podem encadear as operações específicas, mas não devem
  reimplementar internamente as responsabilidades delegadas.
- A integração não deve restringir um M3U a um único formato de áudio nem
  realizar validação por extensão como garantia de compatibilidade; a
  decodificação pertence ao Liquidsoap e aos codecs disponíveis em sua imagem.
- Todos os caminhos de mídia devem permanecer dentro do storage compartilhado
  permitido e devem ser normalizados e validados antes da escrita no M3U.
- Tentativas de acessar mídias fora do storage permitido devem ser rejeitadas.
- `m3u_file_name` deve continuar armazenando somente o nome do arquivo, nunca
  um caminho absoluto ou relativo com diretórios.
- `is_base` substitui o conceito de uma única playlist padrão; não deve existir
  restrição que limite a configuração a somente uma playlist base.
- `weight` deve ser maior que zero e representa uma proporção, não uma
  porcentagem obrigatoriamente totalizada em 100.
- A presença de qualquer agendamento ativo e reproduzível substitui o conjunto
  base durante o intervalo correspondente.
- Um agendamento inválido ou indisponível não deve, sozinho, interromper outras
  playlists válidas do mesmo intervalo.
- Se nenhum agendamento ativo for reproduzível, a configuração deve recorrer
  diretamente ao conjunto base, sem tocar agendamentos expirados ou futuros.
- O fallback final baseado em um arquivo de áudio fixo fica fora do escopo
  desta spec.
- Endpoints HTTP específicos para comandar a geração dos arquivos ficam fora
  do escopo desta spec.
- Iniciar, encerrar, reiniciar ou monitorar o processo do Liquidsoap fica fora
  do escopo desta spec.
- Não devem ser adicionadas dependências nem alteradas versões existentes sem
  necessidade e autorização.
- Todo código Java criado ou alterado deve seguir `docs/code-standards.md`.

## Critérios de aceite

- [ ] Os componentes Java da integração estão organizados em
  `infrastructure/liquidsoap`.
- [ ] Geração de M3U, seleção de playlists, aplicação de pesos, geração da grade,
  persistência e escrita física são executadas por métodos distintos e
  testáveis.
- [ ] Um M3U pode ser gerado a partir de uma mídia, de uma lista de mídias ou de
  um diretório.
- [ ] Um M3U pode referenciar mídias de formatos diferentes sem que a aplicação
  copie ou converta os arquivos.
- [ ] Os M3U são gravados em `/liquidsoap/m3u` e podem ser lidos pelo container
  do Liquidsoap.
- [ ] A playlist armazena em `m3u_file_name` somente o nome do M3U gerado.
- [ ] Várias playlists podem ser marcadas com `is_base` e cada playlist possui
  um `weight` inteiro positivo.
- [ ] Sem agendamento ativo, todas as playlists base válidas são combinadas de
  acordo com seus pesos.
- [ ] Durante um intervalo com agendamentos ativos, as playlists base param e
  somente as playlists agendadas válidas são combinadas.
- [ ] Ao terminar o intervalo agendado, a reprodução retorna às playlists base.
- [ ] Playlists com agendamentos simultâneos participam da mesma combinação
  conforme seus pesos.
- [ ] Agendamentos sobrepostos da mesma playlist não duplicam sua participação
  na combinação.
- [ ] Uma playlist indisponível é ignorada sem interromper outras fontes
  válidas.
- [ ] Quando nenhuma playlist agendada puder ser reproduzida, as playlists
  base válidas são usadas como fallback.
- [ ] A grade e as combinações ponderadas são geradas em arquivos `.liq`
  separados e referenciadas pelo `radio.liq`.
- [ ] Os arquivos gerados não expõem credenciais ou outros dados sensíveis.
- [ ] As migrations e os documentos de estrutura e banco refletem os novos
  campos e componentes.
- [ ] Os testes usam diretórios temporários e não dependem dos volumes Docker
  reais nem de um processo Liquidsoap em execução.
- [ ] O projeto compila e todos os testes passam.
