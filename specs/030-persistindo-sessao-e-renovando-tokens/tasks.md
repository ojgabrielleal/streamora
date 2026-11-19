# Tarefas - Spec 030

- [x] Criar contratos tipados para access token e par interno de tokens.
- [x] Adaptar login para enviar refresh token somente em cookie protegido.
- [x] Implementar renovação e rotação em `POST /auth/refresh`.
- [x] Implementar logout idempotente em `POST /auth/logout`.
- [x] Centralizar configuração e expiração do cookie de refresh.
- [x] Ajustar Spring Security, CORS e proteção contra CSRF.
- [x] Atualizar contratos OpenAPI da autenticação.
- [x] Remover refresh token dos contratos JavaScript do frontend.
- [x] Implementar chamadas de login, refresh e logout com credenciais.
- [x] Implementar renovação compartilhada e retry único no cliente HTTP.
- [x] Restaurar a sessão antes da avaliação das rotas protegidas.
- [x] Integrar o logout do dashboard à API.
- [x] Criar e atualizar testes de backend.
- [x] Criar e atualizar testes de frontend.
- [x] Atualizar exemplos de ambiente e documentação estrutural.
- [x] Executar testes e compilação do backend.
- [x] Executar testes, lint e build do frontend.
- [x] Revisar os critérios de aceite e registrar a validação.

## Validação

- `./mvnw -q -DskipTests compile`: compilação concluída com sucesso.
- Testes focados em autenticação, segurança e OpenAPI: concluídos com sucesso.
- Suíte do backend sem `UnixSocketLiquidsoapProcessControlTests`: 210 testes
  aprovados.
- Suíte completa do backend: 210 testes aprovados e 2 erros na classe
  preexistente `UnixSocketLiquidsoapProcessControlTests`, pois o sandbox não
  permite abrir sockets Unix (`Operation not permitted`).
- `npm test -- --run`: 8 arquivos e 29 testes aprovados.
- `npm run lint`: concluído sem erros ou avisos.
- `npm run build`: TypeScript e bundle de produção concluídos com sucesso.
- Todos os critérios de aceite foram revisados e atendidos. Os atributos do
  cookie são verificados pelos testes automatizados do backend.
