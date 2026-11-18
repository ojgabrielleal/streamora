# Spec 026 - Integrando o frontend à API com TanStack Query

**Status:** Concluída

## Objetivo

Integrar o frontend do Streamora à API Spring Boot por meio de um cliente HTTP
centralizado e do TanStack Query, preparando uma estrutura organizada por
domínio para queries e mutations, com tratamento consistente de respostas,
erros e autenticação, além de permitir as requisições do navegador por meio de
uma configuração segura e parametrizável de CORS no backend.

## Requisitos

### Organização da integração

- Manter a configuração global do TanStack Query dentro de `front-end/src/app`.
- Manter o cliente HTTP compartilhado em `front-end/src/lib/api-client.ts`.
- Criar o diretório `front-end/src/api` para organizar os contratos e as
  operações da API por domínio.
- Organizar cada domínio em seu próprio diretório dentro de
  `front-end/src/api`, evitando concentrar integrações distintas em um único
  arquivo.
- Manter juntas as chaves, funções de requisição e opções reutilizáveis de cada
  query.
- Usar nomes de arquivos que expressem sua responsabilidade, como
  `auth-api.ts`, `auth-mutations.ts` e `auth-types.ts`.
- Não colocar chamadas HTTP diretamente em páginas ou componentes visuais.

### Cliente HTTP

- Evoluir a integração existente para disponibilizar um cliente HTTP
  centralizado e reutilizável.
- Obter a URL base exclusivamente da variável de ambiente `VITE_API_URL`.
- Interromper a requisição com uma mensagem clara quando `VITE_API_URL` não
  estiver configurada.
- Permitir o uso dos métodos HTTP necessários pela API, sem duplicar a lógica
  de montagem das requisições.
- Serializar como JSON os corpos compatíveis e enviar o cabeçalho
  `Content-Type: application/json` quando aplicável.
- Interpretar respostas JSON e tratar corretamente respostas sem conteúdo.
- Considerar respostas fora da faixa de sucesso como erros, preservando ao
  menos o status HTTP e os dados de erro devolvidos pela API.
- Definir um tipo de erro próprio para que páginas, componentes e mutations
  possam tratar falhas de maneira consistente.
- Preservar cabeçalhos informados por cada operação ao combiná-los com os
  cabeçalhos definidos pelo cliente.

### TanStack Query

- Extrair a criação do `QueryClient` para
  `front-end/src/app/query-client.ts`.
- Continuar disponibilizando uma única instância do `QueryClient` por meio do
  `QueryClientProvider` no nível raiz da aplicação.
- Definir opções globais de queries e mutations somente quando representarem
  um comportamento comum a toda a aplicação.
- Não repetir automaticamente requisições quando o erro indicar falha de
  autenticação, autorização ou outra condição que uma nova tentativa não possa
  corrigir.
- Usar `queryOptions` para manter `queryKey` e `queryFn` juntas quando uma
  consulta precisar ser reutilizada.
- Usar chaves de query estáveis, serializáveis e organizadas por domínio.
- Invalidar somente as queries afetadas após mutations bem-sucedidas.
- Manter regras específicas de cache e atualização junto da operação de cada
  domínio, em vez de colocá-las indiscriminadamente na configuração global.

### Autenticação

- Criar o domínio `front-end/src/api/auth` como primeira integração real com a
  API.
- Representar em TypeScript o contrato de entrada do endpoint `POST /login`.
- Representar em TypeScript a resposta contendo `accessToken` e
  `refreshToken`, respeitando o contrato atual do backend.
- Implementar uma mutation de login com TanStack Query usando o cliente HTTP
  compartilhado.
- Preparar o cliente HTTP para receber o access token por um mecanismo
  centralizado e enviá-lo no cabeçalho `Authorization` com o prefixo `Bearer`.
- Não fixar tokens no código-fonte, nas variáveis de ambiente ou em arquivos
  versionados.
- Tratar respostas `401 Unauthorized` de maneira centralizada e previsível.
- Não implementar renovação automática de tokens enquanto a API não possuir
  um contrato próprio para refresh.
- Não criar nesta spec uma estratégia permanente de persistência do refresh
  token no navegador.

### Validação da integração

- Disponibilizar uma forma mínima de executar a mutation de login no frontend
  para validar a comunicação real com `POST /login`.
- A validação poderá usar uma interface provisória e simples, sem antecipar o
  layout definitivo da tela de autenticação.
- Após uma autenticação bem-sucedida, permitir validar ao menos uma requisição
  protegida com o access token recebido.
- Manter a integração de validação isolada dos componentes visuais, usando as
  operações definidas em `front-end/src/api`.
- Exibir estados mínimos de carregamento, sucesso e erro durante a validação.

### CORS

- Configurar CORS no backend por meio do Spring Security.
- Ler as origens permitidas de uma propriedade de configuração alimentada por
  variável de ambiente.
- Documentar a variável de origens permitidas no `.env.example`, com um valor
  local compatível com a porta padrão do Vite.
- Permitir somente os métodos e cabeçalhos necessários à comunicação do
  frontend com a API.
- Permitir o cabeçalho `Authorization` nas requisições do navegador.
- Não liberar todas as origens por padrão.
- Integrar a configuração de CORS à cadeia de segurança existente sem remover
  a autenticação JWT ou ampliar as rotas públicas.
- Garantir o atendimento das requisições de preflight necessárias ao
  navegador.

### Testes, qualidade e documentação

- Criar testes para o comportamento compartilhado do cliente HTTP, cobrindo
  respostas JSON, respostas sem conteúdo e respostas de erro.
- Criar testes para a inclusão do cabeçalho de autenticação quando houver um
  access token disponível.
- Testar a mutation de autenticação sem depender de uma API externa em
  execução.
- Criar testes no backend para a configuração de CORS e sua integração com o
  Spring Security.
- Garantir que lint e build do frontend continuem terminando com sucesso.
- Garantir que os testes e a compilação do backend continuem terminando com
  sucesso.
- Atualizar `front-end/.env.example` e o `.env.example` da raiz quando
  necessário, sem incluir credenciais ou tokens reais.
- Atualizar `docs/project-structure.md` com os novos diretórios e suas
  responsabilidades.

## Regras

- A organização da integração deverá seguir a estrutura `app`, `lib` e `api`,
  sem criar um diretório genérico chamado `features`.
- `lib/api-client.ts` deverá conter somente infraestrutura HTTP compartilhada,
  sem regras específicas de autenticação, playlists, programações ou usuários.
- As operações e os tipos específicos deverão permanecer dentro do respectivo
  domínio em `front-end/src/api`.
- O TanStack Query será responsável pelo estado assíncrono do servidor; não
  deverá ser usado como armazenamento genérico de estado local da interface.
- Os contratos TypeScript deverão refletir os contratos atuais da API, sem
  alterar silenciosamente nomes ou formatos de campos.
- CORS deverá ser configurado no backend; não será usado o modo `no-cors` nem
  outra solução que impeça o frontend de acessar status, cabeçalhos ou corpo da
  resposta.
- A origem permitida deverá ser configurável por ambiente e possuir somente um
  valor local não sensível como padrão de desenvolvimento.
- A implementação não deverá enfraquecer as regras atuais do Spring Security.
- Novas dependências deverão se limitar ao necessário para testar ou sustentar
  a integração definida nesta spec.

## Fora do escopo

- Implementação definitiva das telas de login, playlists, programações,
  usuários, arquivos ou Liquidsoap.
- Integração de todos os endpoints existentes da API.
- Definição da identidade visual ou do layout final do painel.
- Criação de um endpoint de renovação de access token no backend.
- Renovação automática, rotação ou revogação de refresh tokens.
- Definição permanente do armazenamento de tokens no navegador.
- Autenticação por cookies ou alteração do contrato JWT atual.
- Configuração de proxy reverso, domínio, HTTPS ou ambiente de produção.
- Alterações nas regras de negócio dos controllers e services existentes.

## Critérios de aceite

- [x] O `QueryClient` possui configuração central em
  `front-end/src/app/query-client.ts` e uma única instância é fornecida à
  aplicação.
- [x] O cliente HTTP compartilhado está em `front-end/src/lib/api-client.ts` e
  usa `VITE_API_URL` como URL base.
- [x] Respostas JSON, respostas sem conteúdo e erros HTTP são tratados de forma
  consistente pelo cliente.
- [x] Erros da API disponibilizam ao frontend ao menos o status HTTP e os dados
  devolvidos pelo backend.
- [x] As integrações específicas estão organizadas por domínio dentro de
  `front-end/src/api`.
- [x] Nenhuma página ou componente de interface realiza chamadas HTTP
  diretamente.
- [x] O domínio `api/auth` representa os contratos atuais de entrada e saída
  de `POST /login`.
- [x] O login é executado por uma mutation do TanStack Query.
- [x] O cliente envia `Authorization: Bearer <accessToken>` quando um token é
  disponibilizado.
- [x] Uma requisição protegida pode ser concluída após uma autenticação válida.
- [x] Estados mínimos de carregamento, sucesso e erro podem ser verificados no
  fluxo provisório de integração.
- [x] Respostas `401` não entram em tentativas automáticas indevidas.
- [x] O backend aceita requisições da origem de desenvolvimento configurada
  para o frontend.
- [x] Origens não configuradas não recebem permissão de CORS.
- [x] O preflight aceita os métodos e cabeçalhos necessários, incluindo
  `Authorization`.
- [x] A configuração CORS não torna públicos os endpoints protegidos.
- [x] Nenhum token, senha ou outra credencial é incluído em arquivo
  versionado.
- [x] Os testes do cliente HTTP, da autenticação e do CORS terminam com
  sucesso.
- [x] Os comandos de lint e build do frontend terminam com sucesso.
- [x] Os testes e a compilação do backend terminam com sucesso.
- [x] Os arquivos de exemplo de ambiente documentam as configurações
  necessárias sem dados sensíveis.
- [x] `docs/project-structure.md` documenta a nova organização da integração.
