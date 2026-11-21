# Tarefas - Spec 032

- [x] Definir os contratos de dados públicos da conexão FTP e dos itens do storage.
- [x] Configurar host e porta públicos do FTP sem expor segredos.
- [x] Criar o endpoint autenticado de conexão com host, porta e username FTP, sem senha.
- [x] Preservar a árvore da listagem e incluir formato, tamanho e última alteração em cada item.
- [x] Preservar a raiz compartilhada nas operações de listagem, criação, renomeação, movimentação e exclusão.
- [x] Cobrir serviços, storage, contratos e endpoints do backend com testes.
- [x] Extrair o shell autenticado compartilhado sem alterar sessão e logout.
- [x] Registrar a rota protegida `/arquivos` e habilitar seu link na sidebar.
- [x] Criar tipos, cliente, queries e mutations da API de arquivos.
- [x] Criar o cartão de conexão FTP com cópia acessível dos dados públicos.
- [x] Criar breadcrumbs, filtro, estados de consulta e listagem responsiva do diretório.
- [x] Implementar criação de pasta, renomeação, movimentação e exclusão com feedback e confirmação.
- [x] Implementar estados de carregamento, vazio, filtro sem resultado e erro.
- [x] Criar ou atualizar os testes de navegação, componentes, API e interações do frontend.
- [x] Executar as validações aplicáveis.
- [x] Revisar os critérios de aceite.

## Validação

- `./mvnw test`: 231 testes aprovados.
- `npm test`: 39 testes aprovados.
- `npm run lint`: aprovado sem apontamentos.
- `npm run build`: build de produção concluído.
- Revisão visual em desktop concluída, com ajustes no cartão FTP e na distribuição da página.
- Comportamento responsivo revisado na implementação para desktop e dispositivos móveis.
- Critérios de aceite revisados e encerrados em 20 de novembro de 2026.
