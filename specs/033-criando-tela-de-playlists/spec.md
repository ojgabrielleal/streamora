# Spec 033 - Criando tela de playlists

**Status:** Concluída

## Objetivo

Criar uma tela autenticada de Playlists que permita visualizar, buscar, criar,
editar e excluir playlists, além de organizar suas faixas a partir das mídias já
disponíveis no storage, usando somente os contratos existentes do backend.

## Contexto

O backend já oferece operações para listar playlists, consultar seus detalhes,
criar, atualizar, excluir e substituir a lista completa de faixas. O frontend já
consome a listagem no dashboard e possui acesso à árvore de arquivos do storage,
mas Playlists ainda aparece como um destino indisponível na navegação. Falta uma
interface própria que transforme esses recursos em um fluxo claro de curadoria
da programação musical, sem criar endpoints ou alterar regras de negócio.

## Requisitos

### Navegação e estrutura da página

- Disponibilizar a rota autenticada `/playlists`.
- Transformar o item Playlists da sidebar em um link funcional e remover dele a
  indicação `Em breve`.
- Manter Dashboard, Arquivos e Programação com seus comportamentos atuais.
- Indicar visualmente Playlists como opção ativa somente na rota correspondente.
- Reutilizar o shell autenticado, a identidade violeta e fúcsia, os padrões de
  foco e o comportamento responsivo existentes.
- Exibir no cabeçalho o título `Playlists` e uma descrição curta sobre a curadoria
  do repertório da rádio.
- Manter o menu móvel com abertura, fechamento, navegação e logout funcionais.

### Visão geral e descoberta

- Apresentar as playlists em uma biblioteca visual de cartões, com nome,
  quantidade de horários, condição de playlist base, peso e situação das faixas.
- Derivar os indicadores de resumo exclusivamente dos dados retornados pela API,
  incluindo total de playlists, playlists base e playlists com horários.
- Permitir buscar playlists pelo nome sem realizar uma nova chamada a cada tecla.
- Permitir filtrar a biblioteca entre todas, playlists base e playlists com
  horários vinculados.
- Ordenar as playlists alfabeticamente de forma previsível.
- Diferenciar visualmente playlists base sem representar o peso como percentual,
  prioridade absoluta ou garantia de execução.
- Permitir abrir o estúdio de uma playlist a partir de seu cartão.

### Criação e edição de playlists

- Disponibilizar uma ação principal para criar playlist.
- Permitir informar nome, participação no fallback base e peso positivo.
- Explicar em linguagem simples que o peso influencia a combinação realizada pelo
  Auto DJ, sem prometer uma frequência exata de reprodução.
- Permitir editar os mesmos dados de uma playlist existente.
- Preservar o `m3uFileName` atual ao editar metadados e enviar valor nulo na criação,
  deixando a associação do M3U para a operação existente de definição de faixas.
- Validar campos obrigatórios e peso antes do envio, mantendo os controles
  identificados e os erros associados aos respectivos campos.
- Fechar o formulário somente após sucesso e atualizar a biblioteca sem recarregar
  a página.
- Desabilitar ações em andamento para evitar envios duplicados.

### Estúdio de faixas

- Consultar os detalhes da playlist somente quando o usuário abrir seu estúdio.
- Exibir as faixas atuais na ordem retornada pela API.
- Apresentar cada faixa pelo nome do arquivo e seu caminho relativo, sem inventar
  duração, artista, álbum, capa ou outros metadados inexistentes.
- Permitir reordenar faixas por controles explícitos de mover para cima e para
  baixo, utilizáveis por teclado.
- Permitir remover faixas apenas do rascunho local antes de salvar.
- Permitir adicionar mídias por meio de um seletor baseado na árvore retornada pelo
  endpoint existente de storage.
- Exibir diretórios de forma navegável e permitir selecionar somente arquivos;
  formatos não reconhecidos pelo backend podem ser selecionados, mas uma eventual
  rejeição da API deve ser apresentada com mensagem segura.
- Impedir a inclusão duplicada do mesmo caminho no rascunho.
- Salvar a ordem completa pelo contrato de substituição de faixas já existente.
- Exigir ao menos uma faixa antes de salvar, refletindo a validação atual do backend.
- Alertar sobre alterações não salvas antes de fechar ou trocar de playlist.
- Atualizar os detalhes e a biblioteca após uma gravação concluída.

### Exclusão

- Permitir excluir uma playlist por uma ação secundária do cartão ou formulário.
- Exigir confirmação que apresente o nome da playlist e deixe claro que a operação
  não exclui os arquivos de mídia do storage.
- Informar, sem antecipar regras inexistentes, quando a API não permitir a exclusão.
- Remover a playlist da biblioteca após sucesso sem recarregar a página.

### Estados e acessibilidade

- Exibir estados distintos de carregamento, biblioteca vazia, busca sem resultado
  e falha de consulta, com possibilidade de tentar novamente.
- Exibir falhas de consulta e mutação em linguagem segura, sem expor detalhes
  internos do servidor.
- Manter mensagens de sucesso e erro perceptíveis visualmente e anunciadas por
  tecnologias assistivas.
- Garantir que cartões, menus, formulários, confirmações, seletor de arquivos e
  estúdio sejam operáveis por teclado e preservem foco visível.
- Gerenciar foco ao abrir e fechar diálogos e devolver o foco ao acionador.
- Adaptar biblioteca, formulários e estúdio para desktop e dispositivos móveis sem
  rolagem horizontal obrigatória da página.
- Em telas menores, apresentar o estúdio em uma superfície de largura integral;
  em telas maiores, permitir uma composição lado a lado entre faixas e seletor.

### Integração e testes

- Reutilizar `playlistsQueryOptions` para a biblioteca e ampliar o módulo de API de
  playlists somente com os contratos que já existem no backend.
- Reutilizar a consulta da árvore de arquivos já implementada na tela de Arquivos.
- Manter chamadas HTTP dentro de `front-end/src/api` e usar TanStack Query para
  consultas, mutações e invalidação de cache.
- Testar navegação, carregamento, sucesso, vazio, erro, busca, filtros, formulários,
  exclusão e os principais fluxos do estúdio de faixas.
- Atualizar `docs/project-structure.md` caso a implementação crie novos componentes
  ou módulos estruturais.

## Regras

- Não criar nem alterar endpoints, DTOs, entidades, migrations ou serviços do
  backend.
- Não adicionar dependências para implementar a tela.
- Não realizar chamadas HTTP diretamente nos componentes visuais.
- Não persistir rascunhos de formulário, faixas ou credenciais no navegador.
- Não modificar arquivos de mídia ao criar, editar ou excluir playlists.
- Não tratar a posição de uma faixa como garantia de reprodução quando o Auto DJ
  aplicar outras regras de seleção.
- Preservar autenticação, renovação de sessão e logout existentes.
- Manter componentes com responsabilidades claras e evitar abstrações genéricas
  prematuras.

## Fora do escopo

- Criar ou alterar funcionalidades no backend.
- Enviar, baixar, renomear, mover ou excluir arquivos do storage pela tela de
  Playlists.
- Reproduzir áudio, exibir waveform ou editar metadados de mídia.
- Consultar duração, artista, álbum, capa, bitrate ou formato além dos dados já
  disponíveis.
- Criar horários de programação ou implementar a tela de Programação.
- Reproduzir uma playlist em modo de prévia ou aplicá-la imediatamente ao
  Liquidsoap.
- Duplicar playlists, importar ou exportar M3U manualmente.
- Implementar arrastar e soltar como requisito de ordenação.
- Adicionar paginação, seleção em massa ou exclusão em massa.

## Critérios de aceite

- [x] Um usuário autenticado acessa `/playlists` pela sidebar em desktop e mobile.
- [x] A sidebar destaca corretamente Playlists e preserva os demais destinos.
- [x] A biblioteca exibe dados reais, busca, filtros, ordenação e indicadores
  derivados da API, sem métricas fictícias.
- [x] Carregamento, biblioteca vazia, busca sem resultado e falha possuem estados
  distintos e acessíveis.
- [x] O usuário cria e edita playlists com validação, feedback e atualização da
  biblioteca sem recarregar a página.
- [x] A edição de metadados preserva a associação M3U existente.
- [x] O estúdio apresenta as faixas atuais e permite adicionar, remover e reordenar
  um rascunho antes de salvar a lista completa.
- [x] O seletor de mídias reutiliza a árvore do storage, aceita apenas arquivos na
  seleção e impede caminhos duplicados.
- [x] Alterações não salvas recebem confirmação antes de serem descartadas.
- [x] A exclusão exige confirmação nominal, não remove mídias e atualiza a
  biblioteca após sucesso.
- [x] Formulários, confirmações, menus, seletor e controles de ordenação funcionam
  por teclado e possuem gerenciamento de foco adequado.
- [x] A página se adapta a desktop e dispositivos móveis sem rolagem horizontal
  obrigatória.
- [x] Nenhuma dependência ou alteração de backend é necessária.
- [x] Os testes automatizados, o lint e o build do frontend terminam com sucesso.
- [x] A documentação estrutural reflete os novos arquivos relevantes.
