# Plano de implementação - Spec 032

## Objetivo

Evoluir o backend para expor os dados públicos de conexão e operar o storage com isolamento por usuário, completar o contrato com metadados e construir a rota de Arquivos integrada ao layout e à sessão existentes.

## Decisões de implementação

- Obter o username exclusivamente do contexto de segurança para selecionar a pasta FTP e montar os dados de acesso.
- Representar caminhos da API como caminhos relativos à raiz FTP do usuário autenticado.
- Manter a senha fora dos contratos; a interface exibirá `Mesmo acesso do painel` no campo correspondente.
- Fazer a API listar o diretório solicitado, em vez de transferir toda a árvore a cada navegação.
- Acrescentar caminho relativo, tamanho e última alteração ao DTO de item para evitar inferências no frontend.
- Separar consultas e mutações do frontend em módulos de API e React Query, invalidando apenas a listagem afetada.
- Extrair o shell autenticado compartilhado pelo Dashboard e por Arquivos para que sidebar, menu móvel e logout não sejam duplicados.
- Usar componentes focados para cartão de conexão, barra de ações, breadcrumbs, lista de itens e diálogos.
- Preservar dependências e convenções atuais de React, TanStack Router, TanStack Query, Tailwind e Lucide.

## Estrutura proposta

- Alterar as propriedades e a configuração do Pure-FTPd para disponibilizar host e porta públicos ao backend.
- Criar DTO e endpoint autenticado para os dados públicos de conexão FTP.
- Evoluir `FtpFileStorage`, `LocalFtpFileStorage`, `FtpFileService`, mapper, DTOs e controller para isolamento por usuário, listagem por diretório e metadados.
- Criar tipos, cliente, consultas e mutações de storage em `front-end/src/api/files/`.
- Criar componentes da página em `front-end/src/components/files/`.
- Criar a rota `front-end/src/routes/files.tsx` e registrá-la no router.
- Extrair ou adaptar componentes de layout autenticado em `front-end/src/components/layout/`, preservando o comportamento do dashboard.
- Atualizar a sidebar para links reais com estado ativo derivado da rota.
- Criar ou atualizar testes próximos aos módulos Java e frontend alterados.
- Atualizar `.env.example` apenas se novos valores de configuração pública forem necessários.

## Etapas

1. Definir os contratos de conexão e item e ajustar as propriedades públicas do FTP.
2. Tornar a resolução do storage dependente do usuário autenticado e cobrir os limites de segurança com testes.
3. Implementar listagem por diretório com metadados, preservando as mutações existentes.
4. Extrair o shell autenticado e adaptar Dashboard e sidebar sem regressões de navegação ou logout.
5. Criar a camada de API do frontend com chaves de consulta por diretório, mutações e invalidação de cache.
6. Construir a página responsiva com cartão de conexão, breadcrumbs, filtro e listagem de itens.
7. Implementar criação, renomeação, movimentação e exclusão com confirmações, feedback e tratamento de erros.
8. Cobrir navegação, renderização, estados, ações e acessibilidade com testes automatizados.
9. Executar as validações de backend e frontend e revisar todos os critérios de aceite.

## Estratégia de testes

- Testar a resolução da raiz por usuário, bloqueio de travessia, caminhos absolutos, links simbólicos e acesso cruzado.
- Testar listagem e metadados de arquivos e diretórios, inclusive ordenação e diretórios vazios.
- Testar conflitos de nome, arquivos ausentes e falhas de I/O nas operações de organização.
- Testar os endpoints de conexão e storage com usuário autenticado, não autenticado e respostas de erro.
- Testar tipos, serialização, URLs e query params no cliente frontend.
- Testar sidebar ativa, proteção da rota, menu móvel e logout no shell compartilhado.
- Testar cartão FTP, cópia, breadcrumbs, filtro, estados vazio/erro/carregamento e ações do gerenciador.
- Testar atualização da listagem após mutações e prevenção de submissões duplicadas.

## Validação

- Executar `./mvnw test`.
- Executar `npm test` em `front-end/`.
- Executar `npm run lint` em `front-end/`.
- Executar `npm run build` em `front-end/`.
- Verificar manualmente a navegação e as operações em larguras de desktop e mobile com dois usuários distintos.
- Confirmar que nenhum contrato, log ou erro expõe senha ou hash.

## Riscos e cuidados

- O storage atual parte de uma raiz compartilhada; a migração para raiz por usuário deve evitar regressões e jamais ampliar acesso entre usuários.
- O host visto pelo backend pode diferir do host público usado pelo navegador; a configuração deve representar explicitamente o endereço externo.
- A extração do shell não deve alterar a renovação de token, o redirecionamento de usuários não autenticados ou o logout.
- A navegação por diretórios e as mutações devem manter chaves de cache consistentes para não apresentar conteúdo desatualizado.

## Fora do escopo

- Alteração ou recuperação de senha FTP, transferência de arquivos pelo painel, edição e reprodução de arquivos, compartilhamento público, compactação e métricas de capacidade permanecem fora desta implementação.
