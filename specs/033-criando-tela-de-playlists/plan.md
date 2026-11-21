# Plano de implementação - Spec 033

## Objetivo

Implementar a rota autenticada de Playlists como uma biblioteca responsiva com
operações de CRUD e um estúdio de faixas, integrando exclusivamente os endpoints
de playlists e storage já disponíveis.

## Decisões de implementação

- Ampliar os tipos de playlist para representar detalhes e payloads sem alterar o
  contrato já usado pelo dashboard.
- Concentrar consultas e mutações em `front-end/src/api/playlists`, invalidando a
  chave da biblioteca após operações bem-sucedidas.
- Consultar detalhes sob demanda com uma chave que inclua o UUID da playlist.
- Reutilizar a query da árvore do storage no seletor de faixas e calcular caminhos
  relativos durante a navegação da árvore.
- Manter o rascunho de faixas em estado local; a API recebe apenas o estado completo
  quando o usuário confirma o salvamento.
- Usar os recursos de diálogo já disponíveis em `@base-ui/react` ou componentes
  locais simples, sem acrescentar bibliotecas.
- Priorizar controles explícitos para reordenação, preservando teclado e toque sem
  depender de drag and drop.
- Derivar indicadores, busca e filtros no cliente a partir da lista já carregada.

## Estrutura proposta

- Alterar `front-end/src/router.tsx` para registrar `/playlists`.
- Alterar `front-end/src/components/dashboard/dashboard-sidebar.tsx` e
  `front-end/src/components/layout/authenticated-layout.tsx` para o novo destino.
- Alterar `front-end/src/api/playlists/playlist-types.ts`, `playlist-api.ts` e
  `playlist-queries.ts` para detalhes, payloads e mutações existentes no backend.
- Reutilizar `front-end/src/api/files/file-queries.ts` no seletor de mídias.
- Criar `front-end/src/routes/playlists.tsx` como composição da página.
- Criar componentes focados em `front-end/src/components/playlists/` para
  biblioteca, cartão, formulário, confirmação, estúdio e seletor de mídias.
- Criar ou atualizar testes próximos aos módulos, componentes, rota e router.
- Atualizar `docs/project-structure.md` se a nova organização estrutural precisar
  ser documentada.

## Etapas

1. Modelar os contratos frontend de listagem, detalhes, criação, atualização,
   exclusão e substituição de faixas; adicionar queries e mutações com chaves de
   cache consistentes.
2. Registrar a rota protegida e habilitar Playlists na sidebar, incluindo estado
   ativo e comportamento móvel.
3. Construir a biblioteca com indicadores derivados, ordenação, busca, filtros e
   estados de carregamento, vazio, ausência de resultados e erro.
4. Implementar o formulário compartilhado de criação e edição, preservando o M3U
   na edição e invalidando a biblioteca após sucesso.
5. Implementar a confirmação e o fluxo de exclusão com feedback acessível.
6. Implementar o estúdio de faixas com consulta sob demanda, rascunho ordenável,
   proteção contra descarte e salvamento integral.
7. Implementar o seletor navegável de mídias sobre a árvore existente, cálculo de
   caminhos e bloqueio de duplicatas.
8. Refinar responsividade, foco, anúncios, feedbacks e estados de operações em
   andamento.
9. Completar os testes, revisar a documentação estrutural e executar as validações
   finais.

## Estratégia de testes

- Testar as funções de API quanto a método, URL, payload e resposta esperados.
- Testar o router e a sidebar para proteção, navegação, estado ativo e menu móvel.
- Testar a biblioteca em carregamento, sucesso, vazio, erro, busca e filtros.
- Testar criação e edição, incluindo validação, preservação de `m3uFileName`,
  bloqueio durante envio, sucesso e falha.
- Testar confirmação de exclusão e atualização da lista após sucesso.
- Testar carregamento de detalhes, ordem inicial, adição, duplicata, remoção,
  reordenação, lista vazia inválida e payload completo de faixas.
- Testar confirmação de descarte quando o rascunho estiver alterado.
- Testar navegação da árvore e seleção exclusiva de arquivos por teclado.
- Cobrir mensagens acessíveis e gerenciamento de foco nos fluxos críticos.

## Validação

- Executar `npm test` em `front-end/`.
- Executar `npm run lint` em `front-end/`.
- Executar `npm run build` em `front-end/`.
- Revisar manualmente os critérios de aceite em larguras móvel e desktop.
- Confirmar pelo diff que não houve alteração no backend nem inclusão de
  dependências.

## Riscos e cuidados

- A API substitui toda a lista de faixas; falhas não devem descartar o rascunho
  local nem apresentar sucesso parcial.
- O backend rejeita lista vazia e caminhos inválidos; a interface deve antecipar o
  primeiro caso e traduzir com segurança os demais.
- A edição de metadados pode remover a associação com o M3U se não preservar o
  valor retornado pela API.
- A árvore de arquivos pode ser grande; evitar expandir ou renderizar todos os
  níveis simultaneamente sem necessidade.
- Exclusões ou alterações feitas em outra sessão podem tornar detalhes obsoletos;
  erros 404 e conflitos devem manter a interface recuperável.
- O significado de peso não deve ser simplificado como porcentagem ou frequência
  garantida.

## Fora do escopo

- Preservar integralmente os limites definidos na spec, especialmente a ausência
  de mudanças no backend, dependências novas, reprodução de áudio e gerenciamento
  de arquivos.
