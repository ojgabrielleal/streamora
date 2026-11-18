# Plano de implementação — Spec 025

## Objetivo

Criar a base independente do painel web em `front-end/`, com React,
TypeScript e Vite, integrar TanStack Router, TanStack Query, Tailwind CSS e
shadcn/ui e adaptar o serviço `node` existente para executar o ambiente de
desenvolvimento sem depender dos demais serviços do Docker Compose.

## Decisões de implementação

- Inicializar o frontend com React, TypeScript, Vite e `npm`, mantendo
  `package-lock.json` versionado e TypeScript em modo estrito.
- Usar roteamento baseado em código do TanStack Router, suficiente para a rota
  raiz desta etapa e sem adicionar TanStack Start.
- Centralizar `RouterProvider` e `QueryClientProvider` na inicialização da
  aplicação, mantendo a configuração compartilhada fora das páginas.
- Organizar `front-end/src` em responsabilidades pequenas: inicialização da
  aplicação, providers, rotas, componentes de interface e acesso à API.
- Configurar o alias `@/*` para `src/*` no TypeScript e no Vite, preservando o
  padrão utilizado pelo shadcn/ui.
- Inicializar o shadcn/ui e adicionar somente um componente simples à página
  inicial para comprovar a integração, sem antecipar um design system.
- Configurar Tailwind CSS conforme a integração recomendada para a versão
  registrada no lockfile e aplicar classes utilitárias na página inicial.
- Criar uma camada mínima e central de configuração da API que leia
  `VITE_API_URL` por `import.meta.env`, sem implementar contratos do backend
  nem armazenar dados sensíveis.
- Adaptar exclusivamente o serviço `node`: diretório de trabalho
  `/workspace/front-end`, montagem do diretório do frontend, volume próprio
  para `node_modules`, instalação das dependências e execução do Vite em
  `0.0.0.0`.
- Publicar a porta interna do Vite por `FRONTEND_PORT`, com valor padrão, sem
  adicionar dependências do serviço `node` aos demais containers.
- Manter testes automatizados e a configuração de um test runner fora desta
  implementação, conforme a delimitação atual da spec.

## Estrutura prevista

```text
front-end/
├── src/
│   ├── app/
│   │   └── providers.tsx
│   ├── components/
│   │   └── ui/
│   ├── lib/
│   │   ├── api.ts
│   │   └── utils.ts
│   ├── routes/
│   │   └── index.tsx
│   ├── index.css
│   └── main.tsx
├── .env.example
├── components.json
├── eslint.config.js
├── index.html
├── package.json
├── package-lock.json
├── tsconfig.json
├── tsconfig.app.json
├── tsconfig.node.json
└── vite.config.ts
```

A estrutura exata poderá receber arquivos de configuração gerados pelas
ferramentas, desde que suas responsabilidades permaneçam equivalentes e não
sejam adicionados artefatos de testes.

## Etapas

1. Gerar o projeto React com TypeScript e Vite em `front-end/`, instalar as
   dependências estritamente necessárias e revisar os scripts de
   desenvolvimento, build e lint.
2. Configurar os aliases do TypeScript e do Vite, o Tailwind CSS e o
   shadcn/ui, adicionando somente o componente usado na página inicial.
3. Configurar o TanStack Router com rota raiz e página inicial mínima.
4. Criar os providers compartilhados e disponibilizar o TanStack Query no
   nível raiz da aplicação.
5. Criar a configuração central da URL pública da API e documentar
   `VITE_API_URL` no exemplo de ambiente do frontend.
6. Adaptar o serviço `node` no `docker-compose.yml`, incluindo porta
   configurável, montagem do código e volume isolado de `node_modules`.
7. Atualizar o `.env.example` da raiz com `FRONTEND_PORT`, ampliar o
   `.gitignore` para os artefatos do frontend e preservar o tratamento atual
   dos dados sensíveis.
8. Atualizar `docs/project-structure.md` com a nova árvore e as
   responsabilidades do frontend.
9. Criar `tasks.md`, executar lint e build e validar a configuração e a
   inicialização do serviço `node` pelo Docker Compose.

## Validação

- Executar `npm run lint` em `front-end/`.
- Executar `npm run build` em `front-end/` e confirmar a geração do bundle de
  produção.
- Executar `docker compose config` com valores locais não sensíveis para
  validar interpolação, volume e publicação da porta.
- Iniciar o serviço `node` e confirmar que o Vite escuta em `0.0.0.0`, fica
  acessível pela porta publicada e não inicia os outros serviços por
  dependência.
- Verificar que a rota inicial renderiza, usa classes do Tailwind e contém um
  componente do shadcn/ui.
- Verificar que a configuração da API usa somente `VITE_API_URL` e que nenhum
  valor sensível foi incluído em arquivos versionados.
- Executar a suíte Maven existente e a compilação do backend para confirmar
  que a mudança de infraestrutura não introduziu regressões no projeto.

## Fora do escopo

- Configuração de test runner ou criação de testes automatizados do frontend.
- Funcionalidades de negócio, autenticação ou integração completa com a API.
- Identidade visual definitiva e ampliação do catálogo de componentes.
- Renderização no servidor ou adoção do TanStack Start.
- Imagem Docker, proxy reverso ou estratégia de publicação para produção.
- Alterações no código, nos contratos ou nas dependências do backend.
