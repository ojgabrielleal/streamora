# Tarefas - Spec 031

- [x] Criar contrato e propriedades do monitoramento no backend.
- [x] Implementar verificação de disponibilidade do Icecast.
- [x] Expor verificação somente leitura do Liquidsoap.
- [x] Agregar os estados e expor `GET /services/status`.
- [x] Criar cliente e query periódica no frontend.
- [x] Integrar os estados aos cards do dashboard.
- [x] Criar e atualizar testes de backend e frontend.
- [x] Atualizar exemplos de ambiente e documentação estrutural.
- [x] Executar as validações aplicáveis.
- [x] Revisar os critérios de aceite.

## Validação

- `./mvnw -q -DskipTests compile`: concluído com sucesso.
- `./mvnw -q test`: 224 testes aprovados.
- `npm test -- --run`: 10 arquivos e 34 testes aprovados.
- `npm run lint`: concluído sem erros ou avisos.
- `npm run build`: TypeScript e bundle de produção concluídos com sucesso.
- Verificação manual com containers ativos não foi executada neste ambiente; os
  cenários online, offline, falha e isolamento foram cobertos por testes.
