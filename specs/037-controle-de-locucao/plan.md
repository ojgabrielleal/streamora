# Plano de implementação - Spec 037

## Objetivo

Implementar o controle de habilitação da **Locução** no topo da seção **Serviços da rádio** no dashboard, persistindo a configuração em uma tabela genérica `settings` no banco de dados e bloqueando novas autenticações de locutores no Liquidsoap quando a locução estiver desabilitada.

## Decisões de implementação

- Criar a tabela `settings` no modelo chave-valor (`setting_key`, `setting_value`), permitindo armazenar `live_broadcast_enabled` agora e outras configurações globais da rádio no futuro sem novas alterações de estrutura.
- Usar valor padrão habilitado (`true`) caso a chave ainda não exista, garantindo compatibilidade com o comportamento atual.
- Expor endpoints autenticados no back-end para consultar (`GET`) e atualizar (`PUT`) o estado da locução.
- Integrar a verificação no serviço/controller de autenticação do Liquidsoap para recusar autenticações quando a locução estiver desabilitada.
- No front-end, posicionar o card **Locução** acima de **Serviço de Transmissão** e **Serviço de Auto DJ** em `ServiceOverview`, mantendo o mesmo padrão visual e exibindo um controle para habilitar/desabilitar.

## Estrutura proposta

- Banco de dados:
  - Nova migration Flyway em `src/main/resources/db/migration/` criando a tabela `settings` e inserindo a chave inicial.
  - Atualização de `docs/database-schema.md`.
- Back-end:
  - Entidade, repositório e serviço de configurações (`Setting`, `SettingRepository`, `SettingService`).
  - Controller e DTOs para consultar/alterar o estado da locução.
  - Verificação de locução habilitada no fluxo de autenticação do Liquidsoap.
  - Testes unitários/integração para o serviço, controller e bloqueio na autenticação do Liquidsoap.
- Front-end:
  - Serviço de API e hooks para consultar e atualizar o status da locução.
  - Atualização de `front-end/src/components/dashboard/service-overview.tsx` e `front-end/src/routes/dashboard.tsx` para exibir o card **Locução** no topo com o controle de habilitar/desabilitar.
  - Atualização dos testes em `front-end/src/components/dashboard/dashboard.test.tsx`.

## Etapas

1. Criar a migration da tabela `settings` e atualizar `docs/database-schema.md`.
2. Implementar entidade, repositório e serviço de configurações no back-end.
3. Criar os endpoints de consulta e atualização do status da locução e integrar o bloqueio na autenticação do Liquidsoap.
4. Adicionar/atualizar testes do back-end.
5. Implementar a integração na API do front-end e adicionar o card **Locução** no topo de `ServiceOverview`.
6. Atualizar testes do front-end e executar as validações finais.

## Estratégia de testes

- Back-end: testar consulta/atualização do status da locução e verificar que a autenticação do Liquidsoap é recusada quando a locução está desabilitada e permitida quando habilitada.
- Front-end: testar que o card **Locução** é renderizado acima dos demais serviços e exibe os estados `Habilitada` / `Desabilitada` com o controle de alternância.

## Validação

- `./mvnw test` (ou suíte de testes do back-end)
- `npm test` / `npm run build` em `front-end/`

## Riscos e cuidados

- Garantir que o valor padrão da locução seja `true` para não interromper locuções existentes após o deploy.
- Seguir os padrões de código Java (`docs/code-standards.md`) e de banco (`docs/database-standards.md`).

## Fora do escopo

- Derrubar automaticamente uma transmissão ao vivo já conectada no momento em que a locução for desabilitada.
- Habilitar ou desabilitar locução por usuário específico.
