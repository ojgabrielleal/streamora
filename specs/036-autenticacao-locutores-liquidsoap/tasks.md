# Tarefas - Spec 036

- [x] Expor porta do `input.harbor` (`LIQUIDSOAP_HARBOR_PORT`, padrão `8005`) no `docker-compose.yml` e documentar em `.env.example`.
- [x] Implementar endpoint `POST /api/liquidsoap/auth` no back-end para validar credenciais de usuário ativo.
- [x] Liberar `POST /api/liquidsoap/auth` no `SecurityConfig`.
- [x] Atualizar `liquidsoap/radio.liq` com `input.harbor`, função `auth` via HTTP e `fallback(track_sensitive=false, [live, radio])`.
- [x] Exibir dados de conexão ao vivo (host, porta, mountpoint e usuário) no front-end.
- [x] Implementar testes do endpoint de autenticação do Liquidsoap.
- [x] Executar validações (`./mvnw test -Dtest=LiquidsoapAuthServiceTests` e `npm --prefix front-end run build`).
- [x] Revisar critérios de aceite.

## Validação

- `./mvnw test -Dtest=LiquidsoapAuthServiceTests`: 3 testes executados, 0 falhas.
- `npm --prefix front-end run build`: build de produção concluído com sucesso (`tsc -b && vite build`).
- Pendência: teste ponta a ponta conectando um cliente de transmissão externo (BUTT/Mixxx/OBS) no container Liquidsoap em execução.
