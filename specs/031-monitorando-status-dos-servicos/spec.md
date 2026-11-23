# Spec 031 - Monitorando status dos serviços

**Status:** Concluída

## Objetivo

Monitorar a disponibilidade do Icecast e do Liquidsoap no dashboard, substituindo
o estado estático "Não monitorado" por informações atuais obtidas da API.

## Contexto

O dashboard identifica o Icecast como serviço de transmissão e o Liquidsoap
como serviço de Auto DJ, mas atualmente não verifica se esses componentes estão
disponíveis. O backend já compartilha o socket de controle do Liquidsoap e está
na mesma rede Docker do Icecast, portanto deve centralizar as verificações e
expor ao frontend um único contrato de monitoramento.

## Requisitos

### Endpoint de status

- Expor um único endpoint autenticado `GET /services/status`.
- Retornar no mesmo payload os estados independentes de Icecast e Liquidsoap.
- Representar cada serviço com identificador e estado `ONLINE` ou `OFFLINE`.
- Responder com sucesso quando a consulta for processada, mesmo que um ou ambos
  os serviços estejam offline.
- Não permitir que a falha na verificação de um serviço impeça ou altere a
  verificação do outro.
- Documentar o contrato e as respostas no OpenAPI.

### Verificação do Icecast

- Consultar pelo backend o endpoint interno de status do Icecast.
- Considerar o Icecast online somente quando a conexão e a resposta forem
  concluídas com sucesso dentro do tempo limite configurado.
- Manter a URL interna e o tempo limite configuráveis por propriedades e
  variáveis de ambiente, sem expor credenciais ao frontend.

### Verificação do Liquidsoap

- Consultar o socket Unix de controle já compartilhado com o backend.
- Enviar o comando `version` e considerar o Liquidsoap online somente quando a
  resposta completa for recebida dentro do tempo limite configurado.
- Reutilizar a infraestrutura de controle existente sem reiniciar o processo ou
  alterar sua configuração.

### Dashboard

- Fazer uma única requisição ao endpoint de status ao carregar o dashboard.
- Atualizar automaticamente os estados a cada 30 segundos enquanto o dashboard
  estiver em uso.
- Exibir `Verificando` durante a primeira consulta, `Online` ou `Offline` após
  uma resposta válida e `Indisponível` quando a própria consulta à API falhar.
- Exibir o estado correspondente em cada card sem remover nome, descrição ou
  ícone atuais.
- Usar texto, contraste e atributos acessíveis para que o estado não dependa
  apenas de cor.

### Testes e documentação

- Testar as combinações em que ambos estão online, ambos estão offline e apenas
  um serviço está online.
- Testar timeout, resposta inválida e isolamento das verificações no backend.
- Testar carregamento, atualização periódica, sucesso parcial e falha da API no
  frontend.
- Atualizar `.env.example` e a documentação estrutural se novas propriedades ou
  componentes forem adicionados.

## Regras

- O frontend não deve acessar diretamente Icecast nem o socket do Liquidsoap.
- O endpoint não deve retornar endereços internos, credenciais, mensagens de
  exceção ou detalhes de infraestrutura.
- Cada verificação deve usar tempo limite curto para não manter a requisição do
  dashboard bloqueada indefinidamente.
- O monitoramento deve ser somente leitura e não pode alterar o estado dos
  serviços.

## Fora do escopo

- Reiniciar ou controlar serviços pelo dashboard.
- Armazenar histórico de disponibilidade, métricas ou incidentes.
- Enviar alertas ou notificações de indisponibilidade.
- Monitorar banco de dados, FTP, backend ou frontend.
- Exibir quantidade de ouvintes, faixa atual ou estatísticas do Icecast.

## Critérios de aceite

- [ ] Uma única chamada autenticada retorna os estados de Icecast e Liquidsoap.
- [ ] O status de cada serviço é calculado e retornado de forma independente.
- [ ] Icecast e Liquidsoap offline não transformam a resposta do endpoint em
  erro da API.
- [ ] O dashboard apresenta `Verificando`, `Online`, `Offline` e `Indisponível`
  nos cenários correspondentes.
- [ ] O dashboard atualiza os estados automaticamente a cada 30 segundos.
- [ ] O monitoramento não expõe credenciais nem detalhes internos.
- [ ] Testes automatizados cobrem backend e frontend.
