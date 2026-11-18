# Plano de implementação — Spec 026

## Objetivo

Conectar o frontend à API Spring Boot por meio de um cliente HTTP centralizado
e do TanStack Query, validar o fluxo com o login e uma consulta protegida e
configurar CORS no backend sem enfraquecer a autenticação JWT existente.

## Decisões de implementação

- Preservar `front-end/src/app/providers.tsx` como ponto de composição dos
  providers e mover a instância do `QueryClient` para
  `front-end/src/app/query-client.ts`.
- Substituir o helper mínimo atual por `front-end/src/lib/api-client.ts`, com
  montagem de URL, serialização, interpretação de respostas e conversão de
  falhas HTTP em um erro tipado.
- Manter o cliente HTTP independente dos contratos de negócio. Tipos, funções,
  queries e mutations específicos ficarão em `front-end/src/api/<domínio>`.
- Organizar as opções reutilizáveis de consulta com `queryOptions`, mantendo
  `queryKey` e `queryFn` no mesmo módulo.
- Criar `api/auth` para o contrato e a mutation de `POST /login` e
  `api/playlists` somente com a consulta mínima de `GET /playlist`, usada para
  comprovar uma requisição protegida.
- Manter o access token apenas em memória nesta etapa. O cliente consultará um
  provedor central de token antes de cada requisição e adicionará o cabeçalho
  `Authorization` quando houver token disponível.
- Não persistir o refresh token nem implementar renovação automática, pois a
  API ainda não possui endpoint de refresh.
- Tratar `401` como falha não repetível e limpar o access token em memória
  quando uma requisição autenticada perder a autorização.
- Configurar CORS por uma propriedade do Spring alimentada por variável de
  ambiente, aceitando uma lista explícita de origens e usando
  `http://localhost:5173` como padrão local.
- Integrar CORS ao `SecurityFilterChain` atual e permitir preflight sem tornar
  públicos os endpoints de aplicação.
- Adicionar Vitest e os utilitários mínimos de teste para React. Simular
  `fetch` nos testes do frontend, sem introduzir um servidor HTTP de testes ou
  depender do backend em execução.
- Usar a página inicial somente como prova temporária do fluxo: formulário de
  login, estados da mutation e carregamento da lista protegida de playlists.

## Estrutura prevista

```text
front-end/
├── src/
│   ├── api/
│   │   ├── auth/
│   │   │   ├── auth-api.ts
│   │   │   ├── auth-mutations.ts
│   │   │   └── auth-types.ts
│   │   └── playlists/
│   │       ├── playlist-api.ts
│   │       ├── playlist-queries.ts
│   │       └── playlist-types.ts
│   ├── app/
│   │   ├── providers.tsx
│   │   └── query-client.ts
│   ├── lib/
│   │   ├── api-client.ts
│   │   └── access-token.ts
│   └── routes/
│       └── index.tsx
├── vitest.config.ts
└── package.json

src/main/java/com/streamora/streamora/config/
├── CorsConfig.java
└── SecurityConfig.java
```

Os nomes exatos dos arquivos de teste poderão seguir a convenção definida na
configuração do Vitest. O módulo de access token será exclusivamente um estado
volátil e não usará `localStorage`, `sessionStorage`, cookies ou variáveis de
ambiente.

## Contratos considerados

- `POST /login` recebe um JSON com `username` e `password`.
- O login bem-sucedido devolve um JSON com `accessToken` e `refreshToken`.
- Endpoints protegidos esperam o cabeçalho
  `Authorization: Bearer <accessToken>`.
- `GET /playlist` será a operação protegida mínima usada na validação.
- Respostas `204 No Content` não deverão provocar tentativa de interpretar um
  corpo JSON vazio.
- Respostas fora da faixa `2xx` deverão lançar um erro tipado contendo o
  status HTTP e o corpo retornado quando ele estiver disponível.

## Etapas

1. Configurar Vitest para o frontend, adicionar os scripts e dependências de
   desenvolvimento estritamente necessários e separar os arquivos de teste do
   build de produção quando necessário.
2. Criar testes iniciais para o cliente HTTP, cobrindo URL base, corpo JSON,
   preservação de cabeçalhos, respostas JSON, `204`, erros HTTP e ausência da
   variável `VITE_API_URL`.
3. Implementar `lib/api-client.ts`, substituir o helper atual e criar o erro
   tipado compartilhado sem incluir contratos de domínio nesse módulo.
4. Criar o armazenamento volátil do access token e integrar sua leitura ao
   cliente HTTP, com testes para presença e ausência do cabeçalho `Bearer` e
   limpeza após uma resposta autenticada `401`.
5. Extrair o `QueryClient` para `app/query-client.ts`, definir o comportamento
   global de retry para não repetir erros não recuperáveis e preservar uma
   única instância no `QueryClientProvider`.
6. Criar os tipos e a função HTTP de autenticação em `api/auth`, refletindo
   `AuthRequestDTO` e as chaves retornadas por `JwtService`.
7. Criar a mutation de login, armazenar somente o access token em memória após
   sucesso e testar o fluxo com um `QueryClient` isolado por teste.
8. Criar em `api/playlists` o contrato mínimo da listagem, a função HTTP e as
   opções de query de `GET /playlist`, mantendo chave e função colocadas no
   mesmo módulo.
9. Adaptar temporariamente a rota inicial para executar o login, apresentar os
   estados de carregamento e erro e habilitar a consulta de playlists apenas
   quando houver access token.
10. Adicionar a propriedade de origens permitidas ao backend e ao ambiente do
    Docker Compose, com valor local padrão restrito ao Vite.
11. Criar a configuração CORS do Spring com origens, métodos e cabeçalhos
    explícitos e conectá-la ao `SecurityFilterChain` existente.
12. Ampliar `SecurityConfigTests` para validar preflight autorizado, origem
    recusada e preservação da proteção dos endpoints sem token.
13. Atualizar os arquivos `.env.example`, o README do frontend quando o novo
    script precisar ser documentado e `docs/project-structure.md` com as novas
    responsabilidades.
14. Criar `tasks.md`, executar todas as validações e conferir cada critério de
    aceite antes de solicitar a revisão da implementação.

## Estratégia de testes

### Frontend

- Testar o cliente HTTP com `fetch` simulado, sem rede real.
- Confirmar a serialização de corpos JSON e a composição de cabeçalhos.
- Confirmar a leitura de JSON, o retorno correto para `204` e a criação do erro
  tipado em respostas sem sucesso.
- Confirmar que o access token é enviado somente quando estiver disponível.
- Confirmar que a mutation usa `POST /login`, recebe os dois tokens e mantém
  somente o access token no estado volátil.
- Confirmar que erros `401` não recebem retries automáticos.
- Confirmar que a consulta protegida permanece desabilitada antes do login e
  usa uma chave estável depois da autenticação.

### Backend

- Validar um preflight vindo da origem configurada, incluindo método e
  cabeçalho `Authorization`.
- Validar que uma origem ausente da configuração não recebe os cabeçalhos de
  permissão CORS.
- Preservar os testes que comprovam que `/login` é público e os demais
  endpoints permanecem protegidos.
- Confirmar que habilitar CORS não altera a validação do access token.

## Validação

- Executar `npm test` em `front-end/`.
- Executar `npm run lint` em `front-end/`.
- Executar `npm run build` em `front-end/`.
- Executar `./mvnw test`.
- Executar `./mvnw verify` ou a compilação Maven equivalente disponível no
  projeto.
- Executar `docker compose config` para validar a interpolação das variáveis de
  frontend e CORS.
- Iniciar API e frontend no ambiente local e confirmar no navegador que o
  preflight é aceito, o login retorna os tokens e `GET /playlist` recebe o
  access token.
- Confirmar que nenhuma credencial ou token foi gravado nos arquivos
  versionados ou nos exemplos de ambiente.

## Riscos e cuidados

- O access token expira em cinco minutos e não haverá renovação automática;
  após um `401`, o fluxo provisório exigirá novo login.
- O refresh token retornado pelo backend não será persistido nem usado nesta
  etapa, evitando criar um fluxo incompleto antes da existência do endpoint de
  refresh.
- A propriedade de CORS deverá aceitar múltiplas origens sem recorrer ao
  curinga `*`.
- A requisição `OPTIONS` deverá ser atendida pelo suporte CORS, mas essa
  permissão não poderá ser confundida com acesso anônimo aos endpoints finais.
- O cliente HTTP não deverá assumir que toda resposta de sucesso possui JSON.
- A prova visual da integração será deliberadamente mínima e não deverá se
  transformar na tela definitiva de login ou playlists.

## Fora do escopo

- Persistência de access token ou refresh token após recarregar a página.
- Endpoint e fluxo de renovação, rotação ou revogação de tokens.
- Integração dos demais endpoints de playlists ou de outros domínios.
- Telas definitivas, navegação autenticada e controle completo de sessão.
- Proxy reverso, cookies de autenticação, HTTPS e configuração de produção.
- Mudanças nos contratos ou regras de negócio atuais da API.
