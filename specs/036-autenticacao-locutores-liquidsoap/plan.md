# Plano de implementação - Spec 036

## Objetivo

Implementar a autenticação de locutores no Liquidsoap via `input.harbor` integrada ao back-end Spring Boot, expondo a porta de transmissão no Docker e exibindo os dados de conexão no front-end.

## Decisões de implementação

- **Autenticação via `input.harbor` no Liquidsoap:** O locutor conecta no Liquidsoap (porta `8005` por padrão, configurável via variável de ambiente `LIQUIDSOAP_HARBOR_PORT`), e o script usa `auth=fun(login) -> ...` chamando `http.post` para o endpoint interno do Spring Boot (`http://application:8080/api/liquidsoap/auth`).
- **Transição automática:** `fallback(track_sensitive=false, [live, radio])` prioriza a entrada ao vivo (`live`) sobre o AutoDJ (`radio`), mantendo a saída única no `output.icecast` sem derrubar os ouvintes.
- **Credenciais do locutor:** O endpoint valida `username` (e-mail ou username do usuário) e `password` usando o `UserDetailsService` / `PasswordEncoder` existente, garantindo que o usuário esteja ativo (`enabled`).
- **Segurança do endpoint:** O endpoint `/api/liquidsoap/auth` será liberado no `SecurityConfig` apenas para uso do Liquidsoap (ou protegido por segredo/rede interna, sem expor dados sensíveis na resposta).
- **Front-end:** Exibir um card/seção com os dados de conexão ao vivo (host, porta, mountpoint e usuário) na tela de dashboard ou usuários/perfil, consumindo as configurações públicas ou variáveis de ambiente do front-end.

## Estrutura proposta

- `docker-compose.yml` e `.env.example`: expor porta `LIQUIDSOAP_HARBOR_PORT` (padrão `8005`) no serviço `liquidsoap` e passar variáveis de ambiente necessárias.
- `liquidsoap/radio.liq`: adicionar `input.harbor` com função `auth` via HTTP e `fallback(track_sensitive=false, [live, radio])`.
- Back-end (`src/main/java/com/streamora/streamora/`):
  - `controller/LiquidsoapController.java` (ou novo endpoint no controller existente): `POST /api/liquidsoap/auth` recebendo `username` e `password`.
  - `service/LiquidsoapAuthService.java` (ou método no service): validação de credenciais com `UserRepository` e `PasswordEncoder`.
  - `config/SecurityConfig.java`: permitir `POST /api/liquidsoap/auth`.
- Front-end (`front-end/src/`):
  - Exibir informações de conexão ao vivo (host, porta, mountpoint `/live`, usuário logado).

## Etapas

1. Atualizar `docker-compose.yml` e `.env.example` para expor a porta do `input.harbor`.
2. Implementar o endpoint `POST /api/liquidsoap/auth` no Spring Boot, liberar no `SecurityConfig` e criar testes unitários/integração.
3. Atualizar `liquidsoap/radio.liq` com `input.harbor`, validação HTTP e `fallback`.
4. Atualizar o front-end para exibir os dados de conexão ao vivo para o locutor.
5. Validar build/testes do back-end e front-end.

## Estratégia de testes

- Testes de unidade/controller no back-end para `POST /api/liquidsoap/auth`:
  - Credenciais válidas de usuário ativo retornam `200 OK`.
  - Senha inválida, usuário inexistente ou usuário inativo retornam `401 Unauthorized`.
- Validação de build e lint no front-end.

## Validação

- Back-end: `./mvnw test`
- Front-end: `npm run build` (ou `pnpm build`)

## Riscos e cuidados

- Garantir que o Liquidsoap consiga resolver `http://application:8080` na rede Docker `streamora`.
- Garantir que `LIQUIDSOAP_HARBOR_PORT` não conflite com `ICECAST_PORT` (`8000`) ou `APPLICATION_PORT` (`8080`).

## Fora do escopo

- Autenticação direta no Icecast sem passar pelo Liquidsoap.
- Múltiplos locutores simultâneos ou fila de espera.
- Gravação automática das transmissões ao vivo.
