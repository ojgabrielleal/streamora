# Tarefas - Spec 029

- [x] Redirecionar o login bem-sucedido para `/dashboard`.
- [x] Criar a rota protegida `/dashboard`.
- [x] Criar o shell autenticado com menu lateral responsivo.
- [x] Exibir os destinos futuros do menu como indisponíveis.
- [x] Implementar logout com limpeza do access token.
- [x] Criar a visão básica da rádio e do Auto DJ.
- [x] Integrar a query existente de playlists ao dashboard.
- [x] Tratar carregamento, vazio e erro da consulta.
- [x] Exibir a transmissão pública configurada do Icecast.
- [x] Adicionar testes do fluxo autenticado e do dashboard.
- [x] Atualizar a documentação da estrutura do projeto.
- [x] Executar testes, lint e build do frontend.
- [x] Revisar os critérios de aceite e registrar a validação.

## Validação

- `npm test`: 7 arquivos e 24 testes aprovados.
- `npm run lint`: concluído sem erros ou avisos.
- `npm run build`: TypeScript e bundle de produção concluídos com sucesso.
- `git diff --check`: concluído sem erros de whitespace.
- Todos os critérios de aceite foram atendidos e a conclusão da spec foi
  autorizada pelo usuário.
