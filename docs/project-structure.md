# Estrutura do projeto

Este documento descreve a organização atual do Streamora e as convenções a
serem preservadas conforme o projeto crescer.

## Estrutura atual

```text
streamora/
├── docs/
│   ├── code-standards.md
│   ├── database-schema.md
│   └── project-structure.md
├── infrastructure/
│   └── liquidsoap/
│       └── radio.liq
├── specs/
│   └── XXX-nome-da-spec/
│       ├── spec.md
│       ├── plan.md
│       ├── tasks.md
│       └── review.md               # quando necessário
├── src/
│   ├── main/
│   │   ├── java/com/streamora/streamora/
│   │   │   ├── StreamoraApplication.java
│   │   │   ├── entity/
│   │   │   │   ├── type/
│   │   │   │   ├── Playlist.java
│   │   │   │   ├── PlaylistFile.java
│   │   │   │   └── User.java
│   │   │   ├── repository/
│   │   │   │   ├── PlaylistFileRepository.java
│   │   │   │   ├── PlaylistRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   └── service/
│   │   │       ├── PlaylistFileService.java
│   │   │       ├── PlaylistService.java
│   │   │       ├── ResourceNotFoundException.java
│   │   │       └── UserService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/
│   └── test/
│       └── java/com/streamora/streamora/
│           ├── application/
│           │   ├── ApplicationPropertiesTests.java
│           │   └── StreamoraApplicationTests.java
│           ├── entity/
│           │   └── EntityMappingsTests.java
│           ├── migration/
│           │   └── MigrationFilesTests.java
│           ├── repository/
│           │   └── RepositoryStructureTests.java
│           └── service/
│               ├── PlaylistFileServiceTests.java
│               ├── PlaylistServiceTests.java
│               └── UserServiceTests.java
├── .env.example
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

Diretórios gerados, dados persistentes e arquivos sensíveis não fazem parte da
estrutura versionada. O diretório `target/`, volumes Docker e o arquivo `.env`
devem continuar fora do controle de versão.

## Responsabilidades

- `entity`: representa o modelo persistido e seus relacionamentos JPA.
- `entity/type`: contém tipos próprios usados pelas entities, como enums.
- `repository`: define as interfaces de acesso aos dados das entities.
- `service`: concentra operações de negócio e coordena o acesso às repositories.
- `resources/db/migration`: mantém o histórico executável do banco com Flyway.
- `infrastructure`: contém configurações de serviços externos, como Liquidsoap
  e Icecast.
- `specs`: registra requisitos, planejamento, tarefas e revisões de cada mudança.
- `docs`: mantém visões consolidadas da arquitetura e do banco de dados.

Novas camadas devem ser criadas somente quando houver responsabilidade real
para elas. Por exemplo, regras de negócio e coordenação de casos de uso devem
ficar em `service`; endpoints HTTP, quando existirem, devem ficar em
`controller`. Essa evolução evita pacotes vazios e abstrações prematuras.

## Organização dos testes

Os testes espelham a responsabilidade que validam no código principal:

- `application`: inicialização e configuração da aplicação.
- `entity`: convenções e mapeamentos comuns das entities.
- `migration`: convenções globais dos arquivos de migration.
- `repository`: contratos estruturais das repositories.
- `service`: comportamento unitário das operações de negócio, com repositories
  isoladas por mocks.

Testes futuros que dependam de banco devem ficar em
`repository/integration`. Eles devem validar persistência e consultas contra
MySQL, preferencialmente em ambiente isolado, sem misturar essa responsabilidade
com os testes estruturais rápidos.

## Direção arquitetural e SOLID

A estrutura atual segue uma boa direção para SOLID, especialmente pela separação
entre persistência (`entity`) e acesso a dados (`repository`) e pelo uso de
interfaces do Spring Data. Ainda assim, SOLID é avaliado principalmente no
desenho das classes e nas dependências entre elas, não apenas na árvore de
diretórios.

Neste estágio inicial:

- a responsabilidade de cada pacote está delimitada;
- as repositories oferecem abstrações específicas para acesso aos dados;
- não há regras de negócio suficientes para avaliar completamente princípios
  como responsabilidade única, aberto/fechado e inversão de dependência;
- services e controllers não devem acessar detalhes de infraestrutura que não
  pertençam às suas responsabilidades quando forem implementados.

A recomendação é manter classes pequenas, dependências direcionadas por
interfaces quando houver mais de uma implementação ou uma fronteira relevante,
e evitar abstrações criadas apenas para antecipar necessidades futuras.
