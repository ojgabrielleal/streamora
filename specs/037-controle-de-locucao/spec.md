# Spec 037 - Controle de Locução

**Status:** Concluída

## Objetivo

Adicionar o bloco **Locução** no topo da seção **Serviços da rádio** no dashboard, permitindo habilitar ou desabilitar a locução ao vivo com integração ao back-end.

## Contexto

Atualmente, a seção **Serviços da rádio** exibe apenas os cards **Serviço de Transmissão** (Icecast) e **Serviço de Auto DJ** (Liquidsoap) com status somente leitura (`Online` / `Offline`). Embora já exista autenticação de locutores para o Liquidsoap, não há um controle no painel para habilitar ou desabilitar a entrada de locução.

## Requisitos

### Interface (Dashboard)

- Exibir o card **Locução** acima de **Serviço de Transmissão** e **Serviço de Auto DJ** na seção **Serviços da rádio**.
- Exibir o status atual da locução (`Habilitada` ou `Desabilitada`) e um controle para alternar entre habilitar e desabilitar.
- Exibir feedback visual durante o carregamento ou em caso de erro ao consultar/atualizar o status.

### Integração (Back-end)

- Disponibilizar endpoint para consultar o estado atual da locução (habilitada ou desabilitada).
- Disponibilizar endpoint para alterar o estado da locução (habilitar ou desabilitar), persistindo a configuração.
- Recusar novas autenticações de locutores no endpoint de autenticação do Liquidsoap quando a locução estiver desabilitada.

## Regras

- Manter a identidade visual dos cards existentes na seção **Serviços da rádio**.
- Apenas usuários autenticados no painel podem consultar e alterar o status da locução.
- O estado da locução deve ser persistido para se manter após reinicializações do sistema.

## Fora do escopo

- Derrubar automaticamente uma transmissão ao vivo já conectada no momento em que a locução for desabilitada.
- Habilitar ou desabilitar locução por usuário específico.

## Critérios de aceite

- [ ] O bloco **Locução** é exibido acima de **Serviço de Transmissão** e **Serviço de Auto DJ** no dashboard.
- [ ] O painel exibe corretamente o status atual (`Habilitada` ou `Desabilitada`).
- [ ] Ao clicar para habilitar ou desabilitar, o estado é atualizado no back-end e persistido.
- [ ] Quando a locução está desabilitada, o back-end recusa novas autenticações de locutores no Liquidsoap.
