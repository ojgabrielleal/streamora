# Spec 029 - Criando dashboard da rádio

**Status:** Concluída

## Objetivo

Criar a primeira área autenticada do frontend do Streamora, redirecionando o
usuário para um dashboard após o login bem-sucedido. A tela deverá apresentar
uma visão básica da rádio, um resumo dos dados já disponíveis e um menu lateral
responsivo que estabeleça a navegação principal do sistema.

## Requisitos

### Fluxo autenticado e rotas

- Manter a tela de login na rota `/`.
- Criar a rota `/dashboard` para a área autenticada.
- Redirecionar para `/dashboard` imediatamente após um login bem-sucedido.
- Permitir acesso ao dashboard somente quando houver access token em memória.
- Redirecionar para `/` quando a rota protegida for acessada sem uma sessão
  válida, inclusive após recarregar a página.
- Não alterar nesta spec a estratégia atual de armazenamento do access token.
- Disponibilizar uma ação de sair que limpe o access token e retorne ao login.

### Estrutura do dashboard

- Criar um shell autenticado composto por menu lateral e área principal.
- Exibir a marca Streamora no menu lateral usando os assets existentes.
- Destacar visualmente o item correspondente ao dashboard.
- Apresentar no menu os destinos planejados `Dashboard`, `Playlists`,
  `Programação` e `Arquivos`.
- Manter apenas `Dashboard` navegável nesta spec e identificar os demais itens
  como indisponíveis, sem criar páginas vazias ou rotas sem funcionalidade.
- Exibir a ação de sair separada dos itens principais de navegação.
- Usar HTML semântico e fornecer nome acessível aos controles apenas visuais.

### Responsividade do menu

- Manter o menu lateral visível em telas desktop.
- Em telas menores, permitir abrir e fechar a navegação por um controle no
  cabeçalho.
- Fechar o menu móvel após uma ação de navegação ou de saída.
- Impedir que o menu móvel aberto torne o restante da interface confuso para
  teclado ou tecnologias assistivas.
- Preservar foco visível e áreas de toque adequadas em todos os controles.

### Exibição básica da rádio

- Exibir um cartão principal de rádio com identidade visual coerente com a
  tela de login.
- Apresentar o nome `Streamora` e indicar que o Auto DJ é o responsável pelo
  fluxo contínuo.
- Tratar a condição operacional da rádio como informação de interface, sem
  afirmar conexão em tempo real com Icecast ou Liquidsoap.
- Não exibir faixa atual, número de ouvintes, capa, duração ou controles de
  reprodução como dados reais enquanto não existir integração que os forneça.
- Deixar clara, quando necessário, a indisponibilidade desses dados em tempo
  real.

### Resumo de playlists e programação

- Reutilizar a query de playlists já existente para preencher o dashboard.
- Apresentar uma lista curta dos horários recorrentes cadastrados, identificando
  a playlist, o dia da semana e o intervalo de execução.
- Tratar carregamento, lista vazia e falha da consulta com mensagens seguras e
  acessíveis.
- Não realizar chamadas HTTP diretamente nos componentes visuais.
- Não criar endpoints ou alterar contratos do backend para compor o dashboard.

### Identidade visual

- Preservar a linguagem visual em violeta e fúcsia usada na tela de login.
- Criar hierarquia clara entre navegação, cabeçalho, cartão da rádio e resumos.
- Utilizar os componentes, tokens, ícones e dependências já disponíveis no
  frontend.
- Não modificar nem duplicar os arquivos originais da marca.
- Manter o dashboard utilizável em telas móveis e desktop.
- Exibir cartões para os serviços Icecast e Liquidsoap sem afirmar um estado
  operacional enquanto não existir uma consulta de saúde em tempo real.
- Exibir abaixo dos agendamentos a transmissão MP3 pública configurada por
  `VITE_STREAM_URL`, com uma ação para ouvi-la em outra aba.
- Não exibir uma contagem fictícia de ouvintes enquanto não existir integração
  com as estatísticas do Icecast.

### Testes e documentação

- Testar o redirecionamento após o login bem-sucedido.
- Testar a proteção da rota sem access token.
- Testar o logout, incluindo limpeza do token e retorno ao login.
- Testar a renderização do menu lateral e seus estados desktop e móvel.
- Testar carregamento, sucesso, vazio e erro da consulta de playlists.
- Garantir que testes, lint e build do frontend terminem com sucesso.
- Atualizar `docs/project-structure.md` com as novas rotas e componentes.

## Regras

- Reutilizar `playlistsQueryOptions` e o mecanismo atual de access token.
- Manter chamadas da API dentro de `front-end/src/api`.
- Não adicionar dependências para implementar a tela.
- Não alterar o backend, o contrato de autenticação ou as regras de segurança.
- Não persistir access token, refresh token ou credenciais no navegador.
- Não representar dados estáticos ou estimados como informações operacionais
  em tempo real.
- Manter componentes com responsabilidades claras, sem abstrações genéricas
  prematuras.

## Fora do escopo

- Persistência ou renovação da sessão após recarregar a página.
- Integração em tempo real com Icecast ou Liquidsoap.
- Reprodução de áudio no navegador.
- Exibição real da faixa atual, capa, ouvintes, bitrate ou tempo no ar.
- Implementação das telas de playlists, programação e arquivos.
- Cadastro, edição ou remoção de qualquer dado pelo dashboard.
- Criação de novos endpoints ou alterações no backend.
- Papéis e níveis de permissão de usuário.
- Tema escuro.

## Critérios de aceite

- [x] Um login bem-sucedido redireciona o usuário para `/dashboard`.
- [x] Acesso a `/dashboard` sem access token retorna o usuário ao login.
- [x] O dashboard possui menu lateral visível em desktop e acionável em telas
  menores.
- [x] O menu apresenta a marca, destaca `Dashboard` e identifica os destinos
  ainda indisponíveis.
- [x] A ação de sair limpa a sessão em memória e retorna ao login.
- [x] A área principal apresenta uma visão básica da rádio e do Auto DJ sem
  simular dados em tempo real.
- [x] O dashboard apresenta os horários recorrentes a partir da API existente.
- [x] Carregamento, dados vazios e erro da consulta possuem estados claros e
  acessíveis.
- [x] O layout funciona em telas móveis e desktop.
- [x] Nenhuma nova dependência ou alteração de backend é necessária.
- [x] Os testes automatizados, o lint e o build do frontend terminam com
  sucesso.
- [x] `docs/project-structure.md` documenta a nova estrutura.
