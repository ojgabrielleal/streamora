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
├── liquidsoap/
│   ├── radio.liq
│   ├── schedule.liq
│   └── sources.liq
├── front-end/
│   ├── public/images/brand/
│   │   ├── favicon.ico
│   │   ├── logo.png
│   │   └── vertical-logo.png
│   ├── src/
│   │   ├── api/
│   │   │   ├── auth/
│   │   │   └── playlists/
│   │   ├── app/
│   │   │   ├── providers.tsx
│   │   │   └── query-client.ts
│   │   ├── components/
│   │   │   ├── auth/
│   │   │   │   ├── login-form.test.tsx
│   │   │   │   ├── login-form.tsx
│   │   │   │   └── login-submit.ts
│   │   │   └── ui/
│   │   │       └── button.tsx
│   │   ├── lib/
│   │   │   ├── access-token.ts
│   │   │   ├── api-client.ts
│   │   │   └── utils.ts
│   │   ├── routes/
│   │   │   ├── index.tsx
│   │   │   └── root.tsx
│   │   ├── index.css
│   │   ├── main.tsx
│   │   └── router.tsx
│   ├── .env.example
│   ├── components.json
│   ├── package.json
│   ├── package-lock.json
│   ├── vite.config.ts
│   └── vitest.config.ts
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
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── JwtConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── FtpFileController.java
│   │   │   │   ├── LiquidsoapController.java
│   │   │   │   ├── PlaylistController.java
│   │   │   │   ├── ScheduleController.java
│   │   │   │   └── UserController.java
│   │   │   ├── entity/
│   │   │   │   ├── type/
│   │   │   │   ├── Playlist.java
│   │   │   │   ├── Schedule.java
│   │   │   │   └── User.java
│   │   │   ├── dto/
│   │   │   │   ├── auth/
│   │   │   │   │   └── AuthRequestDTO.java
│   │   │   │   ├── ftp/
│   │   │   │   │   ├── FtpDirectoryCreateRequestDTO.java
│   │   │   │   │   ├── FtpFileEntryResponseDTO.java
│   │   │   │   │   ├── FtpMoveRequestDTO.java
│   │   │   │   │   ├── FtpRenameRequestDTO.java
│   │   │   │   │   └── FtpStorageErrorDTO.java
│   │   │   │   ├── liquidsoap/
│   │   │   │   │   └── LiquidsoapErrorDTO.java
│   │   │   │   ├── playlists/
│   │   │   │   │   ├── PlaylistDetailsResponseDTO.java
│   │   │   │   │   ├── PlaylistErrorDTO.java
│   │   │   │   │   ├── PlaylistRequestDTO.java
│   │   │   │   │   ├── PlaylistResponseDTO.java
│   │   │   │   │   ├── PlaylistSummaryDTO.java
│   │   │   │   │   └── PlaylistTracksRequestDTO.java
│   │   │   │   ├── schedules/
│   │   │   │   │   ├── ScheduleRequestDTO.java
│   │   │   │   │   ├── ScheduleResponseDTO.java
│   │   │   │   │   └── ScheduleSummaryDTO.java
│   │   │   │   └── users/
│   │   │   │       ├── UserRequestDTO.java
│   │   │   │       └── UserResponseDTO.java
│   │   │   ├── mapper/
│   │   │   │   ├── FtpFileEntryMapper.java
│   │   │   │   ├── PlaylistMapper.java
│   │   │   │   ├── ScheduleMapper.java
│   │   │   │   └── UserMapper.java
│   │   │   ├── infrastructure/
│   │   │   │   ├── liquidsoap/
│   │   │   │   │   ├── M3uContentReader.java
│   │   │   │   │   ├── UnixSocketLiquidsoapProcessControl.java
│   │   │   │   │   ├── exception/
│   │   │   │   │   ├── interfaces/
│   │   │   │   │   └── records/
│   │   │   │   └── pureFtpd/
│   │   │   │       ├── storage/
│   │   │   │       │   ├── LocalFtpFileStorage.java
│   │   │   │       │   ├── exception/
│   │   │   │       │   │   └── FtpFileStorageException.java
│   │   │   │       │   ├── interfaces/
│   │   │   │       │   │   └── FtpFileStorage.java
│   │   │   │       │   └── records/
│   │   │   │       │       ├── FtpFileEntry.java
│   │   │   │       │       └── FtpFileType.java
│   │   │   │       └── user/
│   │   │   │           ├── PureFtpd.java
│   │   │   │           ├── FtpSystemCommandExecutor.java
│   │   │   │           ├── exception/
│   │   │   │           │   └── FtpUserManagementException.java
│   │   │   │           ├── interfaces/
│   │   │   │           │   └── FtpCommandExecutor.java
│   │   │   │           └── records/
│   │   │   │               └── PureFtpdProperties.java
│   │   │   ├── filter/
│   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   ├── exception/
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── repository/
│   │   │   │   ├── PlaylistRepository.java
│   │   │   │   ├── ScheduleRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   └── service/
│   │   │       ├── AuthService.java
│   │   │       ├── FtpFileService.java
│   │   │       ├── LiquidsoapService.java
│   │   │       ├── PlaylistService.java
│   │   │       ├── ScheduleService.java
│   │   │       ├── JwtService.java
│   │   │       └── UserService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/
│   └── test/
│       └── java/com/streamora/streamora/
│           ├── application/
│           │   ├── ApplicationPropertiesTests.java
│           │   └── StreamoraApplicationTests.java
│           ├── controller/
│           │   ├── AuthControllerTests.java
│           │   ├── FtpFileControllerTests.java
│           │   ├── LiquidsoapControllerTests.java
│           │   ├── PlaylistControllerTests.java
│           │   ├── ScheduleControllerTests.java
│           │   └── UserControllerTests.java
│           ├── config/
│           │   ├── OpenApiDocumentationTests.java
│           │   └── SecurityConfigTests.java
│           ├── entity/
│           │   └── EntityMappingsTests.java
│           ├── factory/
│           │   ├── FactoriesTests.java
│           │   ├── PlaylistFactory.java
│           │   ├── ScheduleFactory.java
│           │   └── UserFactory.java
│           ├── migration/
│           │   └── MigrationFilesTests.java
│           ├── mapper/
│           │   ├── FtpFileEntryMapperTests.java
│           │   ├── PlaylistMapperTests.java
│           │   ├── ScheduleMapperTests.java
│           │   └── UserMapperTests.java
│           ├── infrastructure/
│           │   ├── liquidsoap/
│           │   │   ├── M3uContentReaderTests.java
│           │   │   └── UnixSocketLiquidsoapProcessControlTests.java
│           │   └── pureFtpd/
│           │       ├── storage/
│           │       │   └── LocalFtpFileStorageTests.java
│           │       └── user/
│           │           └── PureFtpdTests.java
│           ├── filter/
│           │   └── JwtAuthenticationFilterTests.java
│           ├── repository/
│           │   └── RepositoryStructureTests.java
│           └── service/
│               ├── AuthServiceTests.java
│               ├── FtpFileServiceTests.java
│               ├── PlaylistServiceTests.java
│               ├── ScheduleServiceTests.java
│               ├── JwtServiceTests.java
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
- `config`: centraliza configurações da aplicação, como a cadeia de filtros
  do Spring Security e os metadados da documentação OpenAPI.
- `controller`: expõe os endpoints HTTP e delega cada operação diretamente ao
  service correspondente, sem tratar ou manipular dados. O controller do
  storage FTP também traduz as exceções próprias dessa integração para o
  contrato HTTP seguro, sem acessar o volume local.
- `entity/type`: contém tipos próprios usados pelas entities, como enums.
- `dto`: define os contratos imutáveis de entrada e saída da aplicação,
  organizados por domínio nos subpacotes `auth`, `ftp`, `liquidsoap`,
  `playlists`, `schedules` e `users`.
- `mapper`: converte entities em DTOs de resposta e aplica DTOs de entrada em
  entities novas ou existentes e também isola a conversão recursiva da árvore
  interna do storage FTP para seu DTO de resposta HTTP.
- `infrastructure`: encapsula a integração da aplicação com serviços e
  utilitários externos.
- `infrastructure/liquidsoap`: valida os caminhos do storage compartilhado,
  gera conteúdos M3U e LIQ, seleciona fontes reproduzíveis, combina playlists
  por peso, lê e grava os arquivos usados pelo Liquidsoap e controla o reinício
  por um socket Unix privado compartilhado entre os containers.
- `infrastructure/pureFtpd/user`: concentra a configuração, a validação de
  credenciais e a execução dos comandos de gerenciamento de usuários do
  Pure-FTPd.
- `infrastructure/pureFtpd/storage`: concentra o contrato e a implementação
  local das operações seguras sobre arquivos e diretórios do volume
  compartilhado com o Pure-FTPd.
- `filter`: intercepta requisições HTTP para autenticar access tokens antes
  que elas alcancem os controllers protegidos.
- `exception`: reúne exceções próprias da aplicação.
- `repository`: define as interfaces de acesso aos dados das entities.
- `service`: concentra operações de negócio e coordena o acesso às repositories
  e aos contratos de infraestrutura, como `FtpFileStorage`. O
  `LiquidsoapService` orquestra `PlaylistService`, `ScheduleService` e os
  componentes da integração, serializa a aplicação da configuração e solicita
  o reinício controlado do Liquidsoap.
- `resources/db/migration`: mantém o histórico executável do banco com Flyway.
- `liquidsoap`: contém a configuração versionada do serviço Liquidsoap.
  `radio.liq` inclui `sources.liq` e `schedule.liq`, habilita o socket Unix e
  registra somente o comando restrito de reinício. Alterações em M3Us são
  recarregadas periodicamente; alterações estruturais são aplicadas por
  `POST /liquidsoap/apply`.
- `front-end`: contém a aplicação web independente em React e TypeScript. O
  diretório `public/images/brand` mantém as logomarcas e o favicon; o
  diretório `app` centraliza providers e a configuração global do TanStack
  Query; `api` organiza contratos, queries e mutations por domínio; `routes`
  define a árvore do TanStack Router; `components/auth` mantém os componentes
  próprios do fluxo de autenticação; `components/ui` mantém os componentes do
  shadcn/ui; `lib` concentra o cliente HTTP, o access token volátil e outros
  utilitários compartilhados; e `index.css` reúne a base do Tailwind CSS e os
  tokens visuais iniciais. O frontend lê a URL pública da API de
  `VITE_API_URL`, usa Vitest nos testes automatizados e é executado pelo serviço
  `node` no ambiente de desenvolvimento.
- `specs`: registra requisitos, planejamento, tarefas e revisões de cada mudança.
- `docs`: mantém visões consolidadas da arquitetura e do banco de dados.

Novas camadas devem ser criadas somente quando houver responsabilidade real
para elas. Por exemplo, regras de negócio e coordenação de casos de uso devem
ficar em `service`; endpoints HTTP, quando existirem, devem ficar em
`controller`. Essa evolução evita pacotes vazios e abstrações prematuras.

## Organização dos testes

Os testes espelham a responsabilidade que validam no código principal:

- `application`: inicialização e configuração da aplicação.
- `config`: comportamento das configurações e integração com o contexto da
  aplicação.
- `controller`: contratos HTTP e delegação direta aos services isolados por
  mocks; os testes do storage também validam os status HTTP e a resposta segura
  para falhas da integração FTP.
- `entity`: convenções e mapeamentos comuns das entities.
- `factory`: centraliza a criação de DTOs e entities fictícios reutilizados
  pelos testes.
- `mapper`: conversão entre DTOs e entities, incluindo campos e
  relacionamentos expostos.
- `infrastructure`: comportamento das integrações externas com seus executores
  isolados, sem exigir que os serviços reais estejam em execução. Os testes de
  usuários e armazenamento do Pure-FTPd espelham os subpacotes `user` e
  `storage`; os testes de armazenamento usam diretórios temporários. Os testes
  do Liquidsoap também usam diretórios temporários para validar M3U, escrita,
  seleção, pesos, sobreposições e fallback sem acessar os volumes reais.
- `filter`: validação isolada dos filtros HTTP, incluindo rejeição e
  autenticação das requisições.
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
