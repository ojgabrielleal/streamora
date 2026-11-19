# Plano de implementação - Spec 030

## Objetivo

Manter a sessão autenticada após recarregar o frontend e renovar os tokens de
forma automática, usando access token apenas em memória e refresh token em
cookie protegido, sem transformar a API stateless em uma sessão de servidor.

## Decisões de implementação

- Manter `POST /login` como entrada da autenticação, mas alterar sua resposta
  para conter somente `accessToken`; o refresh token será enviado em cookie.
- Adicionar `POST /auth/refresh` para validar e rotacionar o refresh token e
  devolver um novo access token.
- Adicionar `POST /auth/logout` para expirar o cookie. A operação será
  idempotente e não dependerá de um access token válido.
- Manter o access token no módulo em memória existente
  `front-end/src/lib/access-token.ts` e não criar persistência no navegador.
- Usar um cookie chamado `streamora_refresh_token`, com `HttpOnly`, `Secure`,
  `SameSite=Lax`, caminho `/auth` e duração máxima de sete dias. Nome e
  atributos ficarão centralizados em uma configuração própria do backend.
- Permitir configurar o atributo `Secure` por ambiente apenas para viabilizar
  desenvolvimento HTTP local; o valor padrão e o ambiente de produção deverão
  mantê-lo habilitado.
- Enviar `credentials: 'include'` nas chamadas de login, renovação e logout e
  habilitar credenciais no CORS somente para as origens já autorizadas.
- Proteger as operações de autenticação baseadas em cookie combinando
  `SameSite`, validação da origem da requisição contra a lista permitida e os
  controles de CORS. Essa validação será aplicada no backend sem adicionar
  dependências.
- Extrair o subject do refresh token validado no `JwtService` para gerar o novo
  par de tokens sem confiar em dados enviados pelo cliente.
- Continuar sem persistir refresh tokens no banco. A rotação substitui o
  cookie do cliente, mas a revogação individual e a detecção de reutilização
  permanecem fora do escopo desta spec.
- Restaurar a sessão uma única vez antes de montar o roteador. Durante essa
  tentativa, o frontend exibirá um estado neutro de carregamento, evitando que
  a guarda envie o usuário ao login prematuramente.
- Centralizar a renovação em uma única Promise compartilhada. Requisições
  concorrentes que recebam `401` aguardarão a mesma renovação.
- Repetir uma requisição autenticada somente uma vez, usando uma marca interna
  no cliente HTTP para impedir ciclos entre `401`, refresh e retry.
- Diferenciar falha de autenticação de falha de rede: apenas uma resposta
  definitiva de renovação inválida limpa o token. No bootstrap, falhas de
  transporte liberam o fluxo público em vez de bloquear toda a aplicação.
- Substituir o logout local do dashboard pelo novo fluxo da API, garantindo a
  limpeza do access token em um bloco de finalização mesmo se a chamada falhar.

## Contratos HTTP propostos

### `POST /login`

- Entrada: o `AuthRequestDTO` atual com usuário e senha.
- Sucesso: `200 OK`, corpo `{ "accessToken": "..." }` e cookie contendo o
  refresh token.
- Falha: `401 Unauthorized` sem cookie válido e sem indicar qual credencial
  falhou.

### `POST /auth/refresh`

- Entrada: sem corpo; refresh token recebido exclusivamente pelo cookie.
- Sucesso: `200 OK`, corpo `{ "accessToken": "..." }` e substituição do cookie
  pelo refresh token rotacionado.
- Falha: `401 Unauthorized` e expiração defensiva do cookie quando ele estiver
  ausente, inválido ou expirado.

### `POST /auth/logout`

- Entrada: sem corpo.
- Resultado: `204 No Content` e cookie expirado, exista ou não uma sessão
  válida.

## Estrutura proposta

```text
src/main/java/com/streamora/streamora/
├── config/
│   ├── AuthCookieProperties.java
│   ├── CorsConfig.java
│   └── SecurityConfig.java
├── controller/
│   └── AuthController.java
├── dto/auth/
│   ├── AuthRequestDTO.java
│   └── AccessTokenResponseDTO.java
└── service/
    ├── AuthService.java
    └── JwtService.java

front-end/src/
├── api/auth/
│   ├── auth-api.ts
│   ├── auth-mutations.ts
│   └── auth-types.ts
├── app/
│   └── session-bootstrap.tsx
├── components/dashboard/
│   └── dashboard-sidebar.tsx
├── lib/
│   ├── access-token.ts
│   └── api-client.ts
├── main.tsx
└── router.tsx
```

Os nomes e a distribuição dos arquivos de teste seguirão os arquivos de
produção correspondentes. A configuração de cookie poderá ser incorporada a
uma configuração de autenticação já existente se isso mantiver melhor o padrão
do projeto sem misturar responsabilidades.

## Etapas

1. Criar o DTO de resposta com somente o access token e adaptar os tipos de
   autenticação do frontend para remover o refresh token do contrato público.
2. Evoluir o `JwtService` para devolver uma representação tipada dos tokens e
   extrair com segurança o usuário de um refresh token validado, preservando
   tipo, assinatura, identificador e expiração.
3. Adaptar o `AuthService` para login e renovação, mantendo nele as regras do
   fluxo e deixando criação e expiração do cookie fora do service.
4. Centralizar a configuração e a montagem do cookie no backend, incluindo
   nome, caminho, duração, `HttpOnly`, `Secure` e `SameSite`.
5. Alterar o `AuthController` para configurar o cookie no login e expor os
   endpoints de refresh e logout com os contratos definidos neste plano.
6. Atualizar o Spring Security para liberar somente os novos endpoints
   públicos e aplicar a validação de origem às operações que usam cookie.
7. Habilitar credenciais no CORS para origens explícitas e atualizar as
   propriedades e os arquivos `.env.example` necessários, sem registrar
   segredos reais.
8. Adaptar `auth-api.ts` para enviar credenciais no login, refresh e logout e
   salvar somente o access token recebido.
9. Implementar no cliente HTTP a renovação compartilhada e a repetição única
   das requisições autenticadas que retornarem `401`.
10. Criar o bootstrap de sessão anterior à montagem do roteador e adaptar a
    guarda para avaliar a rota somente depois dessa tentativa.
11. Integrar o logout do dashboard à API e garantir limpeza local e navegação
    para `/` mesmo diante de falha na chamada.
12. Atualizar testes unitários e de integração, documentação OpenAPI e
    `docs/project-structure.md` conforme os arquivos efetivamente criados.
13. Executar as validações completas de backend e frontend e registrar os
    resultados em `tasks.md` durante a implementação.

## Estratégia de testes do backend

- `JwtServiceTests`: geração dos dois tipos de token, leitura segura do subject,
  rejeição de access token no fluxo de refresh, token inválido e expirado.
- `AuthServiceTests`: login válido, credenciais inválidas, renovação válida e
  falhas de validação sem exposição de detalhes.
- `AuthControllerTests`: corpo e cookie do login, rotação no refresh, expiração
  do cookie nas falhas e logout idempotente.
- `SecurityConfigTests`: acesso público apenas aos métodos e caminhos previstos,
  proteção das demais rotas e rejeição de origens não autorizadas.
- Testes de `CorsConfig`: credenciais habilitadas para origem explícita,
  métodos permitidos e ausência de origem curinga.
- `OpenApiDocumentationTests`: contratos, respostas e segurança documentados
  para login, refresh e logout.
- Testes de propriedades: valores obrigatórios e defaults seguros da
  configuração do cookie.

## Estratégia de testes do frontend

- `auth-api`: uso de `credentials: 'include'`, ausência de refresh token nos
  tipos e contratos corretos para login, refresh e logout.
- `api-client`: envio do access token, refresh após `401`, retry único e
  propagação da resposta final.
- Concorrência: múltiplos `401` autenticados aguardam uma única chamada de
  refresh e repetem suas próprias requisições após o novo token.
- Proteção contra ciclos: `401` no refresh ou no retry não inicia nova
  renovação.
- Bootstrap: sessão válida libera o roteador; sessão ausente ou API
  indisponível conduz ao fluxo público sem bloquear toda a aplicação.
- Guarda da rota: não redireciona durante a restauração e permite o dashboard
  depois que o access token é recuperado.
- Login: armazena somente o access token e continua navegando ao dashboard.
- Logout: chama a API, sempre limpa o token e retorna ao login, inclusive
  quando a chamada falha.

## Validação

- Executar `./mvnw test` ou o comando Maven adotado pelo repositório.
- Executar a compilação do backend sem pular verificações aplicáveis.
- Executar `npm test`, `npm run lint` e `npm run build` em `front-end`.
- Fazer uma verificação manual do fluxo: login, atualização em `/dashboard`,
  expiração do access token, renovação automática, logout e refresh inválido.
- Confirmar no navegador que o refresh token não aparece em storage acessível
  ao JavaScript e que o cookie possui os atributos definidos.
- Revisar todos os critérios de aceite da spec após as validações.

## Riscos e cuidados

- O modo `StrictMode` do React pode executar efeitos duas vezes em
  desenvolvimento; o bootstrap deverá compartilhar a tentativa em andamento
  para não rotacionar o refresh token duas vezes.
- Uma rotação sem persistência no servidor não invalida imediatamente uma
  cópia anterior do JWT. Essa limitação deve permanecer explícita e não pode
  ser apresentada como revogação completa.
- `Secure` impede o cookie em HTTP comum fora das exceções do navegador para
  localhost. A configuração de desenvolvimento deverá ser explícita e nunca
  reduzir a segurança por padrão em produção.
- O caminho do cookie precisa incluir refresh e logout; alterar os endpoints
  exige atualizar criação e expiração com exatamente os mesmos atributos.
- O retry deve reconstruir a requisição sem reutilizar um corpo já consumido.
  O cliente atual trabalha com `json`, o que permite serializar novamente o
  payload com segurança.

## Fora do escopo

- Persistir refresh tokens no banco ou manter uma lista de revogação.
- Detectar reutilização de refresh tokens rotacionados.
- Encerrar todas as sessões de um usuário ou gerenciar dispositivos.
- Alterar a duração atual de cinco minutos e sete dias dos tokens.
- Criar novas telas, redesenhar login ou dashboard ou alterar permissões de
  domínio.
- Adicionar autenticação social, multifator ou opção "lembrar de mim".
