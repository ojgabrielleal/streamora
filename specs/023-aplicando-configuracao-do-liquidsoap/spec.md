# Spec 023 - Aplicando configuração do Liquidsoap

**Status:** Concluída

## Objetivo

Disponibilizar uma operação HTTP autenticada que regenere a configuração do
Liquidsoap com os dados atuais de playlists e agendamentos e faça essa nova
configuração entrar efetivamente em execução por meio do reinício controlado
da própria instância do Liquidsoap.

## Requisitos

### Aplicação da configuração

- Expor `POST /liquidsoap/apply` para aplicar a configuração atual da rádio.
- Ao receber a requisição, consultar as playlists e os agendamentos atuais e
  regenerar `sources.liq` e `schedule.liq` por meio da
  integração criada na Spec 022.
- Manter `radio.liq` como arquivo principal estático, responsável por incluir
  os dois arquivos gerados.
- Depois da escrita bem-sucedida dos arquivos, solicitar que o processo atual
  do Liquidsoap reinicie e carregue novamente `radio.liq` com seus includes.
- Não iniciar uma segunda instância simultânea do Liquidsoap.
- Considerar a operação concluída somente depois que a instância reiniciada
  voltar a responder ao canal de controle.
- Retornar HTTP `204 No Content` quando a configuração nova tiver sido gerada
  e o Liquidsoap estiver novamente disponível.

### Controle do processo

- Habilitar no `radio.liq` um canal de controle por socket Unix.
- Compartilhar somente o diretório do socket entre os containers da aplicação
  e do Liquidsoap por um volume Docker dedicado.
- Registrar no Liquidsoap um comando específico e restrito que execute o
  reinício da aplicação Liquidsoap.
- Implementar na aplicação um cliente responsável exclusivamente por enviar o
  comando de reinício e verificar a recuperação da instância.
- Manter diretamente em `application.properties` o caminho do socket, o tempo
  limite de conexão, o tempo limite total de recuperação e o intervalo entre
  verificações, pois são parâmetros técnicos internos e não sensíveis.
- Reservar variáveis de ambiente e `.env.example` para dados sensíveis, sem
  criar variáveis de ambiente para esses parâmetros técnicos.
- Não montar o Docker socket na aplicação, não executar `docker restart` e não
  expor a interface de controle do Liquidsoap em uma porta TCP.

### Falhas e concorrência

- Não solicitar o reinício quando a geração ou escrita dos arquivos falhar.
- Permitir somente uma execução de `/liquidsoap/apply` por vez na instância da
  aplicação, evitando que duas requisições reescrevam os mesmos arquivos ou
  tentem reiniciar o Liquidsoap simultaneamente.
- Rejeitar imediatamente uma nova aplicação enquanto a execução anterior
  ainda estiver em andamento, sem colocá-la em fila.
- Representar essa rejeição por uma exceção personalizada da aplicação, com
  uma mensagem segura informando que já existe uma aplicação da configuração
  do Liquidsoap em andamento.
- Retornar HTTP `409 Conflict` para uma aplicação concorrente rejeitada.
- Retornar HTTP `503 Service Unavailable` quando o Liquidsoap não puder ser
  contatado, não aceitar o reinício ou não voltar a responder dentro do tempo
  limite configurado.
- Retornar uma mensagem segura de erro, sem expor caminhos internos, comandos,
  stack traces ou conteúdo dos arquivos de configuração.
- Manter os arquivos gerados quando o reinício falhar, permitindo uma nova
  tentativa explícita de `/apply` depois que o Liquidsoap estiver disponível.

### Segurança e contrato HTTP

- Manter o endpoint protegido pelas regras atuais do Spring Security.
- Não disponibilizar o socket de controle fora dos containers que participam
  da integração.
- Documentar no Swagger/OpenAPI a operação e as respostas `204`, `401`, `409`
  e `503`.
- Criar testes do controller com o caso de uso substituído por mock.
- Criar testes unitários do cliente de controle sem iniciar um processo real
  do Liquidsoap.
- Atualizar `docs/project-structure.md` com os componentes adicionados e com a
  responsabilidade do endpoint de aplicação.

## Comportamento esperado

1. O usuário cria ou altera playlists, pesos, marcações de base e horários sem
   reiniciar o Liquidsoap automaticamente.
2. O usuário chama `POST /liquidsoap/apply` quando deseja colocar o conjunto
   atual de alterações no ar.
3. A aplicação regenera os dois arquivos `.liq` incluídos por `radio.liq`.
4. A aplicação envia pelo socket Unix o comando restrito de reinício.
5. O processo do Liquidsoap reinicia com a mesma linha de comando, relê
   `radio.liq` e passa a usar os arquivos recém-gerados.
6. A aplicação aguarda o canal de controle ficar disponível novamente e então
   retorna `204 No Content`.

## Regras

- Alterações no conteúdo de um arquivo M3U já associado continuam sendo
  percebidas pelo `reload` periódico existente e não devem acionar `/apply`
  automaticamente.
- Criação, remoção ou alteração estrutural de playlists e agendamentos somente
  entra em execução após uma chamada explícita a `/liquidsoap/apply`.
- O endpoint não deve reescrever `radio.liq`; esse arquivo permanece
  versionado e estático.
- O controller deve somente delegar a aplicação da configuração e construir a
  resposta HTTP.
- A geração dos `.liq` deve continuar centralizada em `LiquidsoapService` e
  nos componentes existentes, sem duplicar essa lógica no controle do
  processo.
- A comunicação de controle deve aceitar somente comandos definidos pela
  aplicação e nunca receber um comando arbitrário vindo do corpo da requisição.
- Não devem ser adicionadas dependências nem alteradas versões existentes.
- Todo código Java criado ou alterado deve seguir `docs/code-standards.md`.

## Fora do escopo

- Aplicar automaticamente a configuração a cada alteração de playlist ou
  agendamento.
- Controlar containers pela API ou pelo socket do Docker.
- Expor terminal, telnet ou comandos arbitrários do Liquidsoap pela API HTTP.
- Garantir transmissão sem qualquer interrupção durante o reinício.
- Alterar a versão do Liquidsoap ou de outras dependências.

## Critérios de aceite

- [x] `POST /liquidsoap/apply` está autenticado e documentado no OpenAPI.
- [x] A operação regenera `sources.liq` e `schedule.liq` com os dados atuais.
- [x] O reinício somente é solicitado depois da geração bem-sucedida dos dois
  arquivos.
- [x] A instância atual do Liquidsoap é reiniciada sem criar uma segunda
  instância simultânea.
- [x] O Liquidsoap relê `radio.liq` e passa a executar a configuração gerada.
- [x] O sucesso somente é retornado depois que o canal de controle volta a
  responder.
- [x] Aplicações concorrentes são rejeitadas com `409 Conflict`.
- [x] Falhas de comunicação ou recuperação retornam `503 Service Unavailable`
  com uma resposta segura.
- [x] A integração usa socket Unix compartilhado e não concede acesso ao
  socket do Docker.
- [x] Alterações de M3U continuam usando o reload periódico sem reinício
  automático.
- [x] Os testes não dependem de um processo Liquidsoap real.
- [x] `docs/project-structure.md` descreve os componentes e responsabilidades
  adicionados.
- [x] O projeto compila e todos os testes passam.
