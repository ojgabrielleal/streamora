# Tarefas - Spec 033

- [x] Ampliar tipos, funções de API, queries e mutações de playlists.
- [x] Registrar a rota protegida `/playlists` e habilitar o destino na sidebar.
- [x] Implementar a biblioteca com indicadores, busca, filtros e estados de tela.
- [x] Implementar criação e edição com validação e preservação do M3U.
- [x] Implementar confirmação e fluxo de exclusão.
- [x] Implementar o estúdio com rascunho, remoção e reordenação de faixas.
- [x] Implementar o seletor navegável de mídias reutilizando a árvore do storage.
- [x] Proteger alterações não salvas e tratar feedbacks de sucesso e erro.
- [x] Ajustar responsividade, navegação por teclado, foco e anúncios acessíveis.
- [x] Implementar ou atualizar os testes necessários.
- [x] Atualizar a documentação estrutural aplicável.
- [x] Executar as validações aplicáveis.
- [x] Revisar os critérios de aceite.

## Validação

- `npm test`: 14 arquivos e 43 testes aprovados.
- `npm run lint`: aprovado sem achados.
- `npm run build`: aprovado com TypeScript e build de produção do Vite.
- A composição responsiva foi revisada no código para larguras móvel e desktop;
  não foi executada uma inspeção visual em navegador nesta etapa.
- Nenhuma falha preexistente foi identificada durante as validações.
