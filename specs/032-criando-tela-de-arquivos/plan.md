# Plano de implementação - Spec 032

## Objetivo

Evoluir o backend para expor os dados públicos de conexão e completar os itens do storage compartilhado com metadados, além de construir a rota de Arquivos integrada ao layout e à sessão existentes.

## Decisões de implementação

- Obter o username exclusivamente do contexto de segurança para montar os dados de acesso, sem alterar a raiz compartilhada do storage.
- Representar caminhos da API como caminhos relativos à raiz FTP compartilhada.
- Manter a senha fora dos contratos; a interface informará que usuário e senha do FTP são os mesmos usados no painel.
- Preservar a resposta recursiva em árvore da listagem atual.
- Acrescentar formato, tamanho e última alteração ao DTO de item.
- Derivar formato da extensão do arquivo no backend e usar valor nulo para diretórios e arquivos sem extensão.
- Separar consultas e mutações do frontend em módulos de API e React Query, invalidando apenas a listagem afetada.
- Extrair o shell autenticado compartilhado pelo Dashboard e por Arquivos para que sidebar, menu móvel e logout não sejam duplicados.
- Usar componentes focados para cartão de conexão, barra de ações, breadcrumbs, lista de itens e diálogos.
- Preservar dependências e convenções atuais de React, TanStack Router, TanStack Query, Tailwind e Lucide.

## Estrutura proposta

- Alterar as propriedades e a configuração do Pure-FTPd para disponibilizar host e porta públicos ao backend.
- Criar DTO e endpoint autenticado para os dados públicos de conexão FTP.
- Evoluir `LocalFtpFileStorage`, os records, o mapper e o DTO de resposta com os novos metadados, preservando a árvore e o storage compartilhado.
- Criar tipos, cliente, consultas e mutações de storage em `front-end/src/api/files/`.
- Criar componentes da página em `front-end/src/components/files/`.
- Criar a rota `front-end/src/routes/files.tsx` e registrá-la no router.
- Extrair ou adaptar componentes de layout autenticado em `front-end/src/components/layout/`, preservando o comportamento do dashboard.
- Atualizar a sidebar para links reais com estado ativo derivado da rota.
- Criar ou atualizar testes próximos aos módulos Java e frontend alterados.
- Atualizar `.env.example` apenas se novos valores de configuração pública forem necessários.

## Etapas

1. Definir os contratos de conexão e item e ajustar as propriedades públicas do FTP.
2. Implementar o endpoint autenticado que retorna host, porta e username FTP, sem senha.
3. Enriquecer os itens da árvore com formato, tamanho e última alteração, preservando a raiz compartilhada e as mutações existentes.
4. Extrair o shell autenticado e adaptar Dashboard e sidebar sem regressões de navegação ou logout.
5. Criar a camada de API do frontend com chaves de consulta por diretório, mutações e invalidação de cache.
6. Construir a página responsiva com cartão de conexão, breadcrumbs, filtro e listagem de itens.
7. Implementar criação, renomeação, movimentação e exclusão com confirmações, feedback e tratamento de erros.
8. Cobrir navegação, renderização, estados, ações e acessibilidade com testes automatizados.
9. Executar as validações de backend e frontend e revisar todos os critérios de aceite.

## Estratégia de testes

- Testar a permanência na raiz compartilhada e o bloqueio de travessia, caminhos absolutos e links simbólicos.
- Testar listagem e metadados de arquivos e diretórios, inclusive formato, tamanho, última alteração, ordenação e diretórios vazios.
- Testar conflitos de nome, arquivos ausentes e falhas de I/O nas operações de organização.
- Testar os endpoints de conexão e storage com usuário autenticado, não autenticado e respostas de erro.
- Testar tipos, serialização, URLs e query params no cliente frontend.
- Testar sidebar ativa, proteção da rota, menu móvel e logout no shell compartilhado.
- Testar cartão FTP, cópia de host e porta, orientação sobre as credenciais, breadcrumbs, filtro, estados vazio/erro/carregamento e ações do gerenciador.
- Testar atualização da listagem após mutações e prevenção de submissões duplicadas.

## Validação

- Executar `./mvnw test`.
- Executar `npm test` em `front-end/`.
- Executar `npm run lint` em `front-end/`.
- Executar `npm run build` em `front-end/`.
- Verificar manualmente a navegação e as operações em larguras de desktop e mobile, confirmando que usuários autenticados visualizam o mesmo storage.
- Confirmar que nenhum contrato, log ou erro expõe senha ou hash.

## Riscos e cuidados

- O storage deve continuar compartilhado; nenhuma alteração pode segmentar o conteúdo por usuário ou mudar os caminhos já utilizados.
- O host visto pelo backend pode diferir do host público usado pelo navegador; a configuração deve representar explicitamente o endereço externo.
- A extração do shell não deve alterar a renovação de token, o redirecionamento de usuários não autenticados ou o logout.
- A navegação derivada da árvore e as mutações devem manter o cache consistente para não apresentar conteúdo desatualizado.

## Fora do escopo

- Alteração ou recuperação de senha FTP, transferência de arquivos pelo painel, edição e reprodução de arquivos, compartilhamento público, compactação e métricas de capacidade permanecem fora desta implementação.
