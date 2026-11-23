# Spec 036 - Autenticação de locutores no Liquidsoap

**Status:** Concluída

## Objetivo

Permitir que locutores cadastrados no sistema se conectem ao Liquidsoap via `input.harbor` para transmitir ao vivo, com validação de credenciais no back-end e transição automática com o AutoDJ sem interromper os ouvintes no Icecast.

## Contexto

Atualmente o Liquidsoap gerencia apenas a reprodução automática das playlists (AutoDJ) e envia o áudio para o Icecast. Ainda não existe um ponto de entrada para locutores externos (via softwares como BUTT, Mixxx ou OBS) entrarem ao vivo usando suas próprias credenciais de acesso.

## Requisitos

### Liquidsoap (`input.harbor`)

- Configurar `input.harbor` no script do Liquidsoap para receber conexões de transmissão ao vivo.
- Configurar `fallback` com `track_sensitive=false` priorizando a entrada ao vivo (`harbor`) sobre o AutoDJ, retornando automaticamente ao AutoDJ quando o locutor desconectar.
- Validar as credenciais do locutor (usuário e senha/chave de transmissão) consultando o endpoint de autenticação do back-end antes de aceitar a conexão.

### Back-end (Spring Boot)

- Disponibilizar endpoint de autenticação para o Liquidsoap validar usuário e senha/chave de transmissão do locutor.
- Garantir que apenas usuários ativos e autorizados possam autenticar a transmissão ao vivo.

### Infraestrutura (Docker)

- Expor a porta do `input.harbor` no serviço do Liquidsoap no `docker-compose` para permitir conexões externas de softwares de transmissão.

### Front-end

- Exibir os dados de conexão ao vivo (host, porta, mountpoint e usuário/credencial) para o locutor autenticado.

## Regras

- A validação de credenciais no endpoint do Liquidsoap não deve expor dados sensíveis em logs ou respostas.
- A entrada ou saída do locutor ao vivo não deve derrubar a conexão dos ouvintes no Icecast.

## Fora do escopo

- Autenticação direta de locutores no Icecast sem passar pelo Liquidsoap.
- Gerenciamento de múltiplos locutores simultâneos ou fila de espera.
- Gravação automática das transmissões ao vivo.

## Critérios de aceite

- [x] Container do Liquidsoap expõe a porta configurada para o `input.harbor`.
- [x] Endpoint do back-end retorna sucesso para credenciais válidas de locutor ativo e falha para credenciais inválidas ou usuário inativo.
- [x] Locutor consegue conectar ao Liquidsoap via software de transmissão usando suas credenciais.
- [x] Ao conectar, o áudio ao vivo assume a transmissão no Icecast; ao desconectar, o AutoDJ retoma automaticamente.
- [x] Front-end exibe as informações de conexão para o locutor.
