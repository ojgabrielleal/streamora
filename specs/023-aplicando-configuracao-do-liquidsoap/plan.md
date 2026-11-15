# Plano de implementação — Spec 023

## Objetivo

Implementar `POST /liquidsoap/apply` como uma operação autenticada que gera a
configuração atual, solicita diretamente ao Liquidsoap o reinício controlado e
somente confirma o sucesso depois que a instância voltar executando os
arquivos gerados.

## Decisões de implementação

- Manter `radio.liq` estático e passar a gerar `sources.liq` e `schedule.liq`
  pelo fluxo existente em `LiquidsoapService`, renomeando os arquivos atuais
  sem alterar suas responsabilidades.
- Habilitar o command server do Liquidsoap exclusivamente por socket Unix. Um
  volume nomeado `liquidsoap_runtime` será montado nos containers
  `application` e `liquidsoap` para compartilhar apenas esse socket.
- Não publicar porta de controle, não montar `/var/run/docker.sock` e não
  executar comandos Docker pela aplicação.
- Registrar em `radio.liq` um comando de nome fixo, sem argumentos externos,
  que chame `restart()` no próprio processo do Liquidsoap. Assim, a instância
  atual encerra e reinicia com a mesma linha de comando, sem criar uma segunda
  instância concorrente.
- Criar em `infrastructure/liquidsoap` um contrato para controle do processo e
  uma implementação que use as APIs de socket Unix do Java 25, sem adicionar
  dependências.
- Encapsular no cliente de controle o protocolo textual do command server. A
  camada HTTP não poderá escolher nem enviar comandos arbitrários.
- Definir diretamente em `application.properties` o caminho do socket, o tempo
  limite de conexão, o tempo limite total de recuperação e o intervalo entre
  tentativas. Esses valores são parâmetros técnicos internos e não terão
  variáveis correspondentes em `.env.example`; variáveis de ambiente
  continuarão reservadas aos dados sensíveis.
- Tratar a desconexão causada pelo reinício como parte esperada do protocolo.
  Depois de enviar o comando, o cliente abrirá novas conexões até obter uma
  resposta de verificação ou atingir o prazo total.
- Considerar o `/apply` concluído somente quando uma conexão nova comprovar que
  o command server está disponível após o reinício. A conexão usada para
  solicitar o reinício não será aceita como confirmação de recuperação.
- Coordenar geração e reinício em uma nova operação de `LiquidsoapService`.
  Uma trava em memória permitirá somente uma execução por vez na instância da
  aplicação. Se duas requisições chegarem juntas, a primeira continuará e a
  segunda receberá a falha correspondente a `409 Conflict`, sem escrever os
  mesmos arquivos nem enviar outro reinício ao Liquidsoap.
- Criar exceções específicas para aplicação concorrente e indisponibilidade
  do controle. O controller traduzirá essas condições em `409 Conflict` e
  `503 Service Unavailable`, respectivamente, sem expor detalhes internos.
- Criar `LiquidsoapApplyInProgressException` para representar exclusivamente a
  tentativa de iniciar um novo `/apply` enquanto a trava estiver ocupada. A
  exceção terá mensagem segura e será traduzida para `409 Conflict` pelo
  tratamento HTTP do controller.
- Expor `LiquidsoapController` sob `/liquidsoap`, com `POST /apply`, sem corpo
  de requisição e com `204 No Content` após a recuperação confirmada.
- Manter os arquivos gerados caso o reinício ou a recuperação falhem. Uma nova
  chamada explícita poderá reaplicar o mesmo estado quando o Liquidsoap voltar
  a estar acessível.
- Não adicionar validação externa por CLI do Liquidsoap nesta spec. A geração
  continuará restrita aos geradores testados da Spec 022, e falhas de carga
  serão reportadas pela indisponibilidade após a tentativa de reinício.

## Fluxo da operação

1. `LiquidsoapController` recebe `POST /liquidsoap/apply` e delega ao service.
2. `LiquidsoapService` tenta adquirir a trava local de aplicação; se outra
   execução já a possuir, a nova chamada é rejeitada imediatamente.
3. O service consulta playlists e agendamentos e escreve os dois `.liq` pelos
   componentes existentes.
4. Somente após ambas as escritas concluírem, o service solicita o reinício ao
   cliente de controle.
5. O cliente conecta ao socket Unix e envia o comando fixo registrado em
   `radio.liq`.
6. O Liquidsoap executa `restart()`, relê `radio.liq` e seus includes e recria
   o socket de controle.
7. O cliente tenta novas conexões até confirmar a recuperação ou expirar o
   prazo configurado.
8. O service libera a trava em qualquer resultado; o controller
   retorna `204`, `409` ou `503` conforme o caso.

## Etapas

1. Adicionar diretamente em `application.properties` as propriedades técnicas
   imutáveis do canal de controle e criar os testes de configuração, sem
   acrescentá-las a `.env.example`.
2. Ajustar o Compose para declarar e montar `liquidsoap_runtime` nos dois
   serviços, sem porta de controle e sem acesso ao daemon Docker.
3. Configurar o socket Unix e registrar o comando restrito de reinício em
   `radio.liq`.
4. Criar o contrato de controle do processo Liquidsoap, a implementação por
   socket Unix e a exceção de indisponibilidade da integração.
5. Ampliar `LiquidsoapService` com a operação de aplicação, reutilizando a
   geração atual, garantindo a ordem geração/reinício e usando uma trava local
   para rejeitar execuções simultâneas.
6. Criar o DTO seguro de erro e
   `LiquidsoapApplyInProgressException` para a aplicação concorrente.
7. Criar `LiquidsoapController` com `POST /liquidsoap/apply`, os status HTTP e
   a documentação OpenAPI.
8. Criar testes unitários do cliente de controle, do coordenador e do
   controller, cobrindo sucesso, falha de geração, indisponibilidade inicial,
   desconexão durante o reinício, recuperação, timeout e concorrência.
9. Atualizar os testes de segurança e OpenAPI para o novo endpoint.
10. Criar `tasks.md`, atualizar `docs/project-structure.md`, executar toda a
    suíte, executar o empacotamento e revisar os critérios de aceite.

## Estratégia de testes

- Testes de `LiquidsoapService` com o controle mockado comprovarão que a
  geração ocorre antes do reinício e que uma falha de geração impede qualquer
  comando ao Liquidsoap.
- Um teste de concorrência controlado comprovará que somente uma requisição
  executa geração e reinício enquanto a trava estiver ocupada e que a segunda
  tentativa é rejeitada imediatamente.
- Testes do cliente usarão um servidor de socket Unix temporário criado pelo
  próprio teste para simular respostas, desconexão e retorno do command server,
  sem iniciar um processo real do Liquidsoap.
- Os testes do protocolo verificarão que somente o comando constante de
  reinício e o comando constante de verificação são enviados.
- Testes web isolados comprovarão `204`, `409` e `503`, ausência de corpo de
  requisição e mensagens que não revelem socket, comandos ou arquivos.
- Testes de segurança comprovarão `401 Unauthorized` sem autenticação.
- `OpenApiDocumentationTests` comprovará a rota, o método e as respostas
  documentadas.
- Uma inspeção do Compose comprovará que o volume de runtime está limitado aos
  dois serviços e que não existe montagem do socket do Docker.
- Uma validação manual no ambiente Docker confirmará que `/apply` reescreve os
  arquivos, reinicia a instância e somente responde depois que o controle fica
  disponível novamente.
- `./mvnw test` executará a suíte completa e `./mvnw verify -DskipTests`
  confirmará compilação e empacotamento.

## Tratamento de falhas

- Falha na geração ou escrita: propagar a falha existente e não contatar o
  command server.
- Socket ausente ou inacessível antes do comando: retornar `503` e preservar
  os arquivos gerados.
- Desconexão imediatamente depois do comando: continuar para a fase de espera,
  pois o reinício encerra a conexão atual.
- Liquidsoap não volta dentro do prazo: retornar `503` e liberar a trava local.
- Nova chamada durante uma aplicação em andamento: retornar `409` sem gerar
  arquivos nem enviar comandos.
- Em todos os casos, registrar detalhes técnicos somente nos logs e responder
  ao cliente com mensagens seguras.

## Fora do escopo

- Aplicação automática após mutações de playlists ou agendamentos.
- Reload integral sem interrupção da conexão com o Icecast.
- Administração genérica do Liquidsoap ou execução de comandos arbitrários.
- Controle do daemon Docker, alteração de versões ou novas dependências.
- Coordenação distribuída entre várias réplicas da aplicação; o ambiente atual
  possui uma única instância do serviço `application`.
