# Spec 030 - Persistindo sessão e renovando tokens

**Status:** Concluída

## Objetivo

Manter o usuário autenticado ao recarregar ou reabrir o frontend enquanto o
refresh token ainda for válido, renovando o access token automaticamente e
encerrando a sessão de forma segura quando ela não puder mais ser renovada.

## Contexto

Atualmente, o frontend mantém o access token somente em memória. Ao atualizar
a página, esse estado é perdido e a proteção das rotas redireciona o usuário
para o login, mesmo quando o refresh token emitido no login ainda está válido.
Além disso, a API ainda não oferece operações para renovar ou encerrar a
sessão.

## Requisitos

### Contrato de autenticação

- Manter o access token de curta duração somente em memória no frontend.
- Entregar o refresh token por cookie, sem disponibilizá-lo no corpo da
  resposta de login.
- Configurar o cookie do refresh token com os atributos `HttpOnly`, `Secure` e
  `SameSite` adequados ao ambiente e à arquitetura do projeto.
- Limitar o cookie ao menor caminho compatível com as operações de
  autenticação.
- Definir a expiração do cookie de acordo com a validade do refresh token.
- Não armazenar access token ou refresh token em `localStorage` ou
  `sessionStorage`.
- Não disponibilizar o refresh token para leitura pelo JavaScript do frontend.

### Login

- Preservar a autenticação por usuário e senha em `POST /login`.
- Retornar o access token no corpo da resposta após um login bem-sucedido.
- Enviar o refresh token no cookie seguro definido por esta spec.
- Permitir que o navegador receba e envie o cookie nas requisições de
  autenticação feitas pelo frontend.
- Manter respostas de credenciais inválidas sem revelar se o usuário existe.

### Renovação da sessão

- Disponibilizar uma operação pública de renovação da sessão que use
  exclusivamente o refresh token recebido pelo cookie.
- Validar assinatura, tipo, integridade e expiração do refresh token antes de
  emitir novos tokens.
- Emitir um novo access token quando a renovação for válida.
- Rotacionar o refresh token a cada renovação, substituindo o cookie anterior.
- Garantir que access token e refresh token possuam identificadores distintos
  e imutáveis.
- Retornar `401 Unauthorized` sem detalhes sensíveis quando o refresh token
  estiver ausente, inválido, expirado ou não puder renovar a sessão.
- Não aceitar refresh token no corpo da requisição, em query parameter ou no
  cabeçalho `Authorization`.

### Restauração após recarregar a página

- Tentar renovar a sessão durante a inicialização do frontend, antes de
  decidir se uma rota protegida deve redirecionar para o login.
- Manter uma indicação de que a autenticação está sendo restaurada para evitar
  redirecionamento prematuro ou exibição momentânea da tela incorreta.
- Liberar as rotas protegidas após uma renovação bem-sucedida.
- Redirecionar para o login quando a restauração falhar por ausência ou
  invalidade da sessão.
- Não exibir mensagens de erro ao usuário quando a ausência de uma sessão for
  esperada ao acessar a tela de login.

### Renovação durante o uso

- Ao receber `401 Unauthorized` em uma requisição autenticada, tentar renovar
  a sessão antes de encerrar a autenticação do usuário.
- Repetir a requisição original no máximo uma vez após uma renovação
  bem-sucedida.
- Impedir ciclos infinitos de renovação e repetição de requisições.
- Compartilhar uma única tentativa de renovação quando várias requisições
  autenticadas receberem `401 Unauthorized` simultaneamente.
- Não tentar renovar a sessão em falhas do login, da própria renovação ou em
  requisições explicitamente públicas.
- Limpar o access token em memória e redirecionar para o login quando a
  renovação falhar.

### Logout

- Disponibilizar uma operação de logout que expire o cookie do refresh token.
- Limpar o access token em memória no frontend mesmo se a chamada de logout
  não puder ser concluída.
- Redirecionar o usuário para o login após o logout.
- Tornar chamadas repetidas de logout seguras e idempotentes.

### Backend e segurança

- Manter a API stateless e não criar sessão HTTP no servidor.
- Permitir acesso público somente às operações de login, renovação e logout,
  além dos caminhos públicos já existentes.
- Configurar o CORS para aceitar credenciais somente das origens autorizadas.
- Não usar origem curinga quando credenciais estiverem habilitadas.
- Avaliar e aplicar proteção contra CSRF compatível com o uso do cookie nas
  operações que alteram a sessão.
- Centralizar nomes, caminhos, duração e atributos do cookie para evitar
  divergências entre login, renovação e logout.
- Manter chaves, segredos e configurações sensíveis no `.env`, com valores de
  exemplo não sensíveis em `.env.example`.
- Não registrar tokens, cookies, senha ou cabeçalhos de autenticação em logs.

### Testes e documentação

- Testar o login com emissão do access token e configuração do cookie.
- Testar a renovação válida e os casos de cookie ausente, inválido e expirado.
- Testar a rotação do refresh token e a substituição do cookie.
- Testar o logout e a expiração do cookie.
- Testar as permissões das novas operações na configuração do Spring
  Security.
- Testar a configuração de CORS com credenciais e origem autorizada.
- Testar a restauração da sessão durante a inicialização do frontend.
- Testar a repetição única de uma requisição após renovação bem-sucedida.
- Testar falha de renovação, limpeza do estado e redirecionamento para o login.
- Testar requisições `401` concorrentes para garantir uma única renovação.
- Garantir que testes, lint e build do frontend terminem com sucesso.
- Garantir que os testes e a compilação do backend terminem com sucesso.
- Atualizar a documentação OpenAPI dos contratos de login, renovação e logout.
- Atualizar `docs/project-structure.md` se a implementação criar, mover ou
  remover componentes estruturais.

## Regras

- Reutilizar o `JwtService` e o fluxo de autenticação existentes sempre que
  isso não comprometer as regras de segurança desta spec.
- Preservar a validade atual de cinco minutos para o access token e sete dias
  para o refresh token, salvo autorização explícita para alterá-las.
- Não adicionar dependências sem necessidade.
- Não persistir credenciais ou tokens em arquivos ou mecanismos de storage do
  navegador.
- Não redirecionar para o login antes de concluir a tentativa inicial de
  restauração da sessão.
- Não repetir automaticamente requisições que não falharam por autenticação.
- Diferenciar falha de autenticação de falhas de rede ou indisponibilidade da
  API para não tratar todo erro como sessão expirada.
- Seguir os padrões atuais do backend, do frontend e de seus testes.

## Fora do escopo

- Alterar os prazos de validade definidos para os tokens.
- Implementar login social, autenticação multifator ou recuperação de senha.
- Criar gerenciamento de dispositivos ou uma tela com sessões ativas.
- Adicionar a opção "lembrar de mim".
- Alterar as regras de autorização ou os perfis de acesso do usuário.
- Persistir uma lista de refresh tokens ou implementar revogação global de
  sessões, salvo se isso for aprovado em uma especificação própria.
- Redesenhar as telas de login ou dashboard.

## Critérios de aceite

- [x] Um usuário autenticado permanece em uma rota protegida após atualizar a
  página enquanto o refresh token for válido.
- [x] A inicialização aguarda a restauração da sessão antes de decidir pelo
  redirecionamento ao login.
- [x] O access token permanece somente em memória.
- [x] O refresh token é enviado somente por cookie protegido e não aparece no
  corpo das respostas.
- [x] O login retorna um access token e configura o cookie do refresh token.
- [x] Uma renovação válida emite novo access token e rotaciona o refresh token.
- [x] Refresh token ausente, inválido ou expirado resulta em `401 Unauthorized`
  sem exposição de detalhes sensíveis.
- [x] Uma requisição autenticada que recebe `401 Unauthorized` é repetida no
  máximo uma vez após uma renovação bem-sucedida.
- [x] Respostas `401 Unauthorized` simultâneas compartilham uma única tentativa
  de renovação.
- [x] Falha na renovação limpa o estado autenticado e direciona o usuário ao
  login sem criar ciclos de requisição.
- [x] O logout limpa o access token, expira o cookie e direciona o usuário ao
  login.
- [x] CORS aceita credenciais apenas para origens autorizadas.
- [x] As operações baseadas em cookie possuem proteção contra CSRF compatível
  com o fluxo adotado.
- [x] Nenhum token ou credencial é salvo em `localStorage`, `sessionStorage`,
  arquivos ou logs.
- [x] Os contratos de autenticação estão documentados no OpenAPI.
- [x] Os testes automatizados de backend e frontend cobrem os fluxos de
  sucesso, falha e concorrência descritos nesta spec.
- [x] Testes, lint, build e compilação aplicáveis terminam com sucesso.
