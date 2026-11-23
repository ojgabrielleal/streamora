# Spec 025 - Setup inicial do front-end

**Status:** Concluída

## Objetivo

Preparar a base do painel web do Streamora em um diretório `front-end` na raiz
do projeto, usando React com TypeScript, Vite, TanStack Router, TanStack Query,
Tailwind CSS e shadcn/ui, e integrar sua execução ao serviço `node` já
existente no Docker Compose.

## Requisitos

### Estrutura e ferramentas

- Criar o diretório `front-end/` na raiz do projeto.
- Inicializar o projeto com React, TypeScript e Vite.
- Usar `npm` como gerenciador de pacotes e versionar o arquivo
  `package-lock.json`.
- Manter o código-fonte da interface dentro de `front-end/src`.
- Disponibilizar scripts para desenvolvimento, build e lint.
- Configurar o TypeScript em modo estrito e preservar os aliases definidos
  pelo shadcn/ui.
- Não alterar as versões do Java, Spring Boot ou das dependências existentes
  da API.

### TanStack

- Configurar TanStack Router como solução de roteamento da aplicação.
- Criar uma rota raiz e uma página inicial mínima para validar o roteamento.
- Configurar TanStack Query como solução para requisições, cache e estado
  assíncrono da API.
- Disponibilizar o `QueryClientProvider` no nível raiz da aplicação.
- Manter a configuração dos providers centralizada, evitando sua duplicação
  nas páginas.
- Não usar TanStack Start nesta etapa.

### Estilização e componentes

- Configurar Tailwind CSS para processar os arquivos do frontend.
- Inicializar o shadcn/ui com suporte ao alias de componentes.
- Adicionar somente os componentes necessários para comprovar o setup básico.
- Criar uma página inicial simples que utilize ao menos um componente do
  shadcn/ui e classes do Tailwind CSS.
- Manter o tema inicial simples, sem definir nesta spec a identidade visual
  definitiva do produto.

### Integração com a API

- Configurar a URL pública da API por meio de uma variável de ambiente do
  Vite, sem fixar endereço no código-fonte.
- Documentar essa variável em um arquivo de exemplo versionado.
- Não armazenar tokens, senhas ou outros dados sensíveis no frontend ou em
  arquivos versionados.
- Preparar uma camada central para acesso à API, sem implementar nesta spec os
  contratos de negócio do backend.
- Considerar que o código executado no navegador acessa a porta publicada da
  API, e não o hostname interno do serviço Docker.

### Docker Compose

- Reutilizar o serviço `node` já existente em `docker-compose.yml`.
- Alterar o diretório de trabalho do serviço para o diretório do frontend.
- Fazer o serviço instalar as dependências necessárias e iniciar o servidor de
  desenvolvimento do Vite.
- Configurar o Vite para escutar em todas as interfaces dentro do container.
- Publicar a porta de desenvolvimento do Vite, permitindo configuração por
  variável de ambiente com um valor padrão adequado.
- Montar o código do frontend no container para permitir atualização durante o
  desenvolvimento.
- Preservar `node_modules` em um volume próprio, evitando que a montagem do
  código do host o substitua.
- Manter o serviço na rede `streamora` e preservar os demais serviços do
  Docker Compose sem mudanças funcionais.
- Garantir que a inicialização do frontend não dependa da disponibilidade do
  MySQL, Icecast, Liquidsoap ou Pure-FTPd.

### Qualidade e documentação

- Garantir que lint e build possam ser executados por scripts do npm.
- Manter arquivos gerados, dependências instaladas, cobertura e variáveis
  locais fora do controle de versão.
- Atualizar `.env.example` com as variáveis necessárias ao serviço do
  frontend, sem incluir valores sensíveis.
- Atualizar `docs/project-structure.md` com a estrutura e as responsabilidades
  do novo diretório.

## Regras

- O Spring Boot continuará responsável exclusivamente pela API, autenticação,
  regras de negócio e integrações do backend.
- O frontend será uma aplicação independente consumidora da API HTTP.
- O serviço `node` existente será adaptado; não deverá ser criado outro serviço
  de frontend no Docker Compose.
- O ambiente do Docker Compose será voltado ao desenvolvimento. A estratégia
  de imagem e publicação para produção será definida em outra spec.
- Novas dependências deverão se limitar ao necessário para React, Vite,
  TanStack Router, TanStack Query, Tailwind CSS, shadcn/ui e lint.
- A configuração deve seguir as recomendações vigentes das ferramentas nas
  versões registradas pelo `package-lock.json` no momento da implementação.
- A página inicial servirá apenas para validar a infraestrutura e não deverá
  antecipar funcionalidades de negócio.

## Fora do escopo

- Implementação de login, armazenamento de tokens ou controle de acesso.
- Criação das telas de playlists, programações, usuários, arquivos ou
  Liquidsoap.
- Integração completa com endpoints da API.
- Definição definitiva de layout, marca, tema ou design system.
- Renderização no servidor e adoção do TanStack Start.
- Configuração de proxy reverso, domínio, HTTPS, CDN ou deploy de produção.
- Criação de uma imagem Docker de produção para o frontend.
- Alterações nos contratos ou no código Java da API.
- Configuração de ambiente e criação de testes automatizados para o frontend.

## Critérios de aceite

- [ ] O projeto React com TypeScript existe em `front-end/` e inicia sem erros.
- [ ] O servidor Vite executa pelo serviço `node` existente no Docker Compose.
- [ ] A aplicação pode ser aberta no host pela porta publicada do Vite.
- [ ] Alterações no código-fonte são refletidas no ambiente de desenvolvimento.
- [ ] TanStack Router renderiza corretamente a rota inicial.
- [ ] TanStack Query está configurado por um provider compartilhado.
- [ ] Tailwind CSS aplica estilos na página inicial.
- [ ] A página inicial usa ao menos um componente do shadcn/ui.
- [ ] A URL da API é obtida de uma variável de ambiente documentada.
- [ ] Nenhum dado sensível é versionado pelo setup.
- [ ] Os scripts de lint e build terminam com sucesso.
- [ ] O build de produção do Vite é gerado com sucesso.
- [ ] Os outros serviços do Docker Compose permanecem funcionais e sem mudanças
  de comportamento.
- [ ] `.env.example` e `docs/project-structure.md` refletem a nova configuração.
