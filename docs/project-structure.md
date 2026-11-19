# Estrutura do projeto

Consulte este documento somente ao criar, mover ou remover componentes
estruturais. Atualize-o quando a organização descrita abaixo mudar.

## Visão geral

```text
streamora/
├── docs/           # documentação técnica e padrões
├── front-end/      # aplicação React e TypeScript
├── liquidsoap/     # configuração do fluxo de áudio
├── specs/          # requisitos, planos, tarefas e revisões
├── src/main/       # backend Spring Boot e recursos
├── src/test/       # testes do backend
├── .env.example
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

Arquivos sensíveis, dados persistentes, volumes Docker, `.env`, `target/` e
outros artefatos gerados não devem ser versionados.

## Backend

O código Java fica em `src/main/java/com/streamora/streamora/`:

- `config`: configurações da aplicação e integrações com o Spring.
- `controller`: endpoints HTTP e tradução do contrato da API.
- `development`: inicializações exclusivas do ambiente de desenvolvimento.
- `dto`: contratos imutáveis de entrada e saída, agrupados por domínio.
- `entity`: modelo persistido e tipos relacionados.
- `exception`: exceções próprias da aplicação.
- `filter`: filtros HTTP, incluindo autenticação.
- `infrastructure`: integrações com Liquidsoap, Pure-FTPd e armazenamento.
- `mapper`: conversão entre entities, DTOs e modelos de infraestrutura.
- `repository`: acesso aos dados.
- `service`: regras de negócio e coordenação dos casos de uso.

Recursos ficam em `src/main/resources/`; migrations Flyway, em
`src/main/resources/db/migration/`.

## Frontend e áudio

- `front-end/src/api`: contratos e operações HTTP por domínio.
- `front-end/src/app`: providers e configuração global.
- `front-end/src/components`: componentes por domínio e componentes de UI.
- `front-end/src/lib`: cliente HTTP e utilitários compartilhados.
- `front-end/src/routes`: rotas da aplicação.
- `liquidsoap`: scripts versionados do auto-DJ e controle do fluxo de áudio.

## Testes

- Testes Java ficam em `src/test/java/com/streamora/streamora/` e espelham os
  pacotes do código principal.
- `factory` mantém dados fictícios reutilizáveis; `migration` valida convenções
  globais; integrações com banco devem ficar em `repository/integration`.
- Testes de infraestrutura devem isolar serviços externos e usar diretórios
  temporários quando manipularem arquivos.
- Testes do frontend ficam próximos dos componentes, rotas ou módulos testados.

## Regras estruturais

- Crie uma camada somente quando existir uma responsabilidade real para ela.
- Mantenha endpoints em `controller`, negócio em `service`, persistência em
  `repository` e integrações externas em `infrastructure`.
- Prefira classes pequenas e interfaces em fronteiras relevantes ou quando
  houver mais de uma implementação.
- Evite pacotes vazios e abstrações para necessidades apenas futuras.
