# Plano de implementação - Spec 027

## Objetivo

Transformar a rota `/` na tela de login definitiva do Streamora, reutilizando
a integração de autenticação existente e os assets de marca, sem antecipar o
dashboard ou uma estratégia de persistência de sessão.

## Decisões de implementação

- Manter `/` como a rota de login para evitar mudanças desnecessárias no
  roteamento atual.
- Usar `vertical-logo.png` como imagem principal por seu formato mais adequado
  a um formulário centralizado; o arquivo será servido por
  `/images/brand/vertical-logo.png`.
- Remover da página a query de playlists e todo o conteúdo de validação
  provisória da spec anterior.
- Reutilizar `loginMutationOptions`, que já envia as credenciais e disponibiliza
  o access token em memória após o sucesso.
- Extrair um `LoginForm` para `src/components/auth` para separar o formulário
  da composição da página e permitir testes focados, sem criar uma abstração
  genérica prematura.
- Manter estado local somente para usuário e senha; carregamento, sucesso e
  erro continuarão derivados da mutation do TanStack Query.
- Usar campos nativos estilizados com Tailwind e o `Button` já existente, sem
  instalar novos componentes ou dependências.
- Após sucesso, substituir o formulário por uma confirmação simples. A futura
  navegação autenticada será definida junto da primeira rota protegida.

## Layout proposto

- Fundo neutro ocupando toda a viewport.
- Cartão central com largura limitada e espaçamento adaptável.
- Logo vertical acima do título e da orientação de acesso.
- Campos de usuário e senha em uma única coluna.
- Botão de entrada ocupando a largura disponível.
- Mensagem de erro próxima ao formulário e confirmação no mesmo cartão.

## Etapas

1. Criar o componente `LoginForm` com campos acessíveis, validação nativa,
   atributos de autocomplete e submissão pela mutation existente.
2. Tratar os estados de carregamento, erro e sucesso sem expor detalhes da API
   e sem permitir submissões concorrentes.
3. Simplificar `HomePage`, removendo a consulta de playlists e compondo a nova
   tela com a logo e o formulário.
4. Criar testes do fluxo visual com a mutation mockada, cobrindo renderização,
   credenciais enviadas, carregamento, erro e sucesso.
5. Atualizar `docs/project-structure.md` com o componente e o teste adicionados.
6. Executar `npm test`, `npm run lint` e `npm run build` dentro de
   `front-end`.
7. Revisar todos os critérios de aceite e registrar o resultado em
   `tasks.md` durante a implementação.

## Estratégia de testes

- Confirmar que a logo, os rótulos, os campos e o botão são renderizados.
- Preencher usuário e senha e verificar que a mutation recebe exatamente essas
  credenciais.
- Simular mutation pendente para validar texto de carregamento e botão
  desabilitado.
- Simular falha para validar a mensagem segura com semântica de alerta.
- Simular sucesso para validar a confirmação e a remoção do formulário.
- Executar a suíte existente para evitar regressões na integração com a API.

## Fora do escopo

- Criar dashboard, menu, shell autenticado ou rota protegida.
- Redirecionar o usuário após o login.
- Persistir ou renovar tokens.
- Alterar o backend ou o contrato de autenticação.
- Adicionar bibliotecas de formulário, validação ou testes.
- Modificar as imagens da marca.
