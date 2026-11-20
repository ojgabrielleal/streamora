# Plano de implementação - Spec 031

## Objetivo

Criar um endpoint autenticado que verifica Icecast e Liquidsoap de forma
independente e integrar o dashboard a esse contrato com atualização automática
a cada 30 segundos.

## Decisões de implementação

- Expor `GET /services/status` com o contrato:

```json
{
  "icecast": "ONLINE",
  "liquidsoap": "OFFLINE"
}
```

- Usar um enum compartilhado pelos DTOs do backend com os valores `ONLINE` e
  `OFFLINE`; os nomes dos campos identificam os serviços sem expor detalhes
  internos.
- Retornar `200 OK` sempre que o backend conseguir processar a requisição. A
  indisponibilidade dos serviços será parte do payload, não um erro HTTP.
- Consultar o Icecast com o cliente HTTP da JDK, usando URL e timeout definidos
  em propriedades `icecast` e variáveis de ambiente.
- Considerar o Icecast online apenas para uma resposta HTTP `2xx` válida do
  endpoint interno `/status-json.xsl`; conexão, timeout, resposta inválida ou
  outro status resultam em `OFFLINE`.
- Expor no contrato `LiquidsoapProcessControl` uma verificação somente leitura,
  reutilizando o comando `version` e o timeout de conexão existentes.
- Manter as verificações independentes. Cada integração converte suas falhas em
  indisponibilidade sem propagar detalhes técnicos ao controller.
- Criar uma query TanStack dedicada, com `refetchInterval` de 30 segundos. O
  componente priorizará `Verificando` na carga inicial e `Indisponível` quando
  a chamada à API falhar, mesmo que exista dado anterior em cache.

## Estrutura proposta

- Backend:
  - criar DTO e enum do contrato em `dto/services`;
  - criar propriedades e verificador HTTP do Icecast em `infrastructure/icecast`;
  - ampliar `LiquidsoapProcessControl` e
    `UnixSocketLiquidsoapProcessControl` com a consulta de disponibilidade;
  - criar `RadioServiceStatusService` para reunir os dois resultados;
  - criar `ServiceStatusController` para `GET /services/status` e OpenAPI;
  - registrar propriedades e valores padrão em `StreamoraApplication`,
    `application.properties`, `.env.example` e `docker-compose.yml`.
- Frontend:
  - criar tipos, função de API e query em `front-end/src/api/services`;
  - adaptar `ServiceOverview` para consumir a query e renderizar os estados;
  - preservar a estrutura visual e tornar as mensagens acessíveis.
- Testes:
  - adicionar testes de propriedades, verificadores, service e controller no
    backend;
  - adicionar testes da API, query e componente de serviços no frontend;
  - atualizar testes existentes do dashboard.
- Documentação:
  - atualizar `docs/project-structure.md` somente se a descrição das camadas
    precisar mencionar os novos componentes.

## Etapas

1. Definir o enum, o DTO de resposta e as propriedades configuráveis do Icecast.
2. Implementar e testar a verificação HTTP do Icecast com timeout.
3. Tornar pública no contrato de controle a verificação somente leitura do
   Liquidsoap e testar sucesso, resposta inválida e timeout.
4. Implementar o service agregador e garantir todas as combinações de estado.
5. Expor e documentar o endpoint autenticado no controller.
6. Criar tipos, cliente e query periódica no frontend.
7. Integrar a query aos cards e implementar os quatro estados visuais.
8. Atualizar exemplos de ambiente, documentação e testes afetados.
9. Executar as validações completas e revisar os critérios de aceite.

## Estratégia de testes

- Backend:
  - validar configuração e valores obrigatórios das propriedades do Icecast;
  - simular `2xx`, status não `2xx`, timeout, falha de conexão e resposta
    inválida do Icecast;
  - validar resposta completa, incompleta e timeout no socket do Liquidsoap;
  - cobrir `ONLINE/ONLINE`, `ONLINE/OFFLINE`, `OFFLINE/ONLINE` e
    `OFFLINE/OFFLINE` no service;
  - confirmar `200 OK`, payload e delegação no controller;
  - confirmar que o endpoint exige autenticação.
- Frontend:
  - validar URL, autenticação e desserialização da função de API;
  - usar temporizadores falsos para verificar o intervalo de 30 segundos;
  - validar `Verificando`, `Online`, `Offline`, sucesso parcial e `Indisponível`;
  - preservar os nomes, descrições, ícones e atributos acessíveis dos cards.

## Validação

- Executar `./mvnw test` e `./mvnw -q -DskipTests compile`.
- Executar `npm test -- --run`, `npm run lint` e `npm run build` em `front-end`.
- Verificar manualmente o payload com cada serviço ativo e inativo.
- Confirmar no dashboard a carga inicial e pelo menos uma atualização automática.
- Revisar todos os critérios de aceite da spec.

## Riscos e cuidados

- O endpoint interno do Icecast pode responder sem uma fonte transmitindo; o
  status mede disponibilidade do serviço, não existência de ouvintes ou áudio.
- Os timeouts devem impedir bloqueio prolongado sem gerar falsos positivos.
- A leitura do socket não pode interferir no comando de reinício existente.
- Falhas de monitoramento não devem registrar credenciais nem retornar mensagens
  internas ao frontend.
- A atualização periódica deve ser cancelada quando o dashboard for desmontado,
  comportamento fornecido pela query TanStack vinculada ao componente.

## Fora do escopo

- Controle ou reinício dos serviços, histórico, métricas, alertas e notificações.
- Monitoramento de outros componentes ou estatísticas de transmissão.
- Alterações na autenticação, no áudio ou na programação do Auto DJ.
