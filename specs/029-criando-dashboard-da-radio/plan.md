# Plano de implementação - Spec 029

## Objetivo

Introduzir a primeira área autenticada do Streamora com redirecionamento após
o login, proteção de rota, menu lateral responsivo e uma visão básica da rádio
alimentada somente pelos dados que a API já disponibiliza.

## Decisões de implementação

- Manter `/` como login e adicionar `/dashboard` ao TanStack Router.
- Usar `beforeLoad` na rota do dashboard para verificar o access token em
  memória e redirecionar acessos não autenticados para `/`.
- Navegar para `/dashboard` no sucesso da mutation dentro do `LoginForm`, sem
  mover a regra HTTP para o componente.
- Criar um shell próprio do dashboard para separar navegação e conteúdo da
  página.
- Montar o menu com `LayoutDashboard`, `ListMusic`, `CalendarDays`, `Folder`
  e `LogOut`, reutilizando `lucide-react`, que já está instalado.
- Exibir os destinos ainda não implementados como itens desabilitados com a
  indicação `Em breve`, evitando rotas artificiais.
- Manter o menu fixo em desktop e usar um painel sobreposto com backdrop em
  telas menores, controlado por estado local e com atributos acessíveis.
- Limpar o token por `clearAccessToken` antes de navegar ao login na ação de
  saída.
- Consultar playlists por `useQuery(playlistsQueryOptions())` e derivar no
  frontend a lista de horários recorrentes, sem nova API.
- Apresentar o cartão `No ar` como composição visual do produto e descrever o
  Auto DJ sem afirmar telemetria real. Dados de faixa e ouvintes não serão
  simulados.
- Usar a logo horizontal no menu e preservar o gradiente violeta/fúcsia da
  tela de login como identidade do dashboard.

## Layout proposto

- Menu lateral escuro em violeta profundo, com logo, navegação principal e
  ação de sair no rodapé.
- Cabeçalho compacto com saudação neutra e botão de menu no mobile.
- Cartão principal em gradiente para a visão da rádio, com selo `Auto DJ` e
  mensagem de fluxo contínuo.
- Seção inferior `Agendado`, inspirada em painéis de web-rádio, com uma lista
  curta de playlists, dias da semana e intervalos de execução.
- No desktop, grade inspirada em painéis de web-rádio: a coluna principal e
  mais larga reúne a visão da rádio e os agendamentos; a coluna auxiliar reúne
  os serviços Icecast e Liquidsoap.
- Usar uma área de conteúdo mais ampla e espaçamentos generosos para aproveitar
  melhor monitores desktop sem comprometer o empilhamento em telas menores.
- Exibir abaixo de `Agendado` um bloco de transmissões com a única saída MP3
  atual do Liquidsoap, usando a URL pública configurável `VITE_STREAM_URL`.
- Fundo claro com detalhes suaves da identidade visual, mantendo contraste e
  leitura confortáveis.

## Estrutura proposta

```text
front-end/src/
├── components/
│   └── dashboard/
│       ├── dashboard-sidebar.tsx
│       ├── dashboard-sidebar.test.tsx
│       ├── radio-overview.tsx
│       └── playlist-overview.tsx
├── routes/
│   ├── dashboard.tsx
│   └── dashboard.test.tsx
└── router.tsx
```

A distribuição exata dos testes poderá ser simplificada se um arquivo de
teste por fluxo oferecer melhor legibilidade, sem alterar as responsabilidades
dos componentes de produção.

## Etapas

1. Adaptar o formulário para navegar ao dashboard após o sucesso, preservando
   os estados de erro e carregamento atuais.
2. Registrar a rota `/dashboard` e adicionar a verificação de access token
   antes de carregá-la.
3. Criar o shell responsivo com menu lateral, navegação futura desabilitada e
   logout.
4. Criar a visão básica da rádio sem representar telemetria inexistente.
5. Integrar a query existente de playlists e derivar a lista curta de horários
   exibida na página.
6. Criar testes para autenticação, proteção, logout, navegação responsiva e
   estados da query.
7. Atualizar `docs/project-structure.md` com os componentes e rotas criados.
8. Executar `npm test`, `npm run lint` e `npm run build` em `front-end`.
9. Revisar os critérios de aceite e registrar a validação em `tasks.md` durante
   a implementação.

## Estratégia de testes

- Simular sucesso da mutation de login e verificar a navegação para
  `/dashboard`.
- Executar a guarda com e sem access token e verificar o destino correto.
- Acionar logout e confirmar que o token foi removido antes do retorno a `/`.
- Renderizar o shell e verificar marca, item ativo, itens futuros e controles
  acessíveis do menu móvel.
- Simular query pendente, com erro, vazia e preenchida.
- Fornecer playlists com diferentes `schedules` para confirmar a ordenação e
  os dados exibidos.
- Executar toda a suíte existente para detectar regressões no login e no
  cliente HTTP.

## Fora do escopo

- Implementar páginas acessadas pelos demais itens do menu.
- Persistir a autenticação ou usar o refresh token.
- Criar player de áudio ou monitoramento em tempo real.
- Alterar endpoints, entidades ou serviços do backend.
- Adicionar bibliotecas de gráficos, layout ou gerenciamento de estado.
