# Plano de implementação — Spec 020

## Objetivo

Implementar o gerenciamento seguro dos arquivos e diretórios do volume local
compartilhado com o Pure-FTPd, mantendo a coordenação dos casos de uso no
service, isolando as operações de `java.nio.file` atrás de uma interface e
separando os componentes de usuários dos componentes de armazenamento.

## Etapas

### 1. Reorganizar a integração existente de usuários

- Mover `PureFtpd` e `FtpSystemCommandExecutor` para
  `infrastructure/pureFtpd/user`.
- Mover os subpacotes existentes `interfaces`, `records` e `exception` para
  dentro de `infrastructure/pureFtpd/user`.
- Atualizar packages e imports da aplicação e mover `PureFtpdTests` para o
  pacote de testes correspondente, preservando assinaturas e comportamento.
- Manter `PureFtpdProperties` como a configuração única da integração; o
  armazenamento consumirá seu `homeDirectory` sem duplicar configuração.

### 2. Definir o contrato de armazenamento

- Criar `FtpFileStorage` em
  `infrastructure/pureFtpd/storage/interfaces` com operações para:
  listar a árvore, excluir/renomear/mover arquivos e
  criar/renomear/excluir diretórios vazios.
- Criar o record imutável `FtpFileEntry` em
  `infrastructure/pureFtpd/storage/records`, com nome, tipo e filhos.
- Representar o tipo do item com um enum próprio (`FILE` e `DIRECTORY`) no
  subpacote de storage, evitando valores textuais livres.
- Criar `FtpFileStorageException` em
  `infrastructure/pureFtpd/storage/exception` para erros explícitos de
  validação, acesso, colisão e manipulação do volume.

### 3. Implementar o armazenamento local seguro

- Criar `LocalFtpFileStorage` como componente Spring baseado exclusivamente em
  `java.nio.file`, recebendo `PureFtpdProperties` e usando `homeDirectory` como
  raiz permitida.
- Centralizar a resolução dos caminhos relativos: rejeitar valores absolutos,
  vazios quando incompatíveis com a operação, segmentos `..` e qualquer
  resultado normalizado fora da raiz.
- Validar a raiz e os caminhos reais dos itens existentes sem seguir links
  simbólicos indevidamente; rejeitar links simbólicos ou ancestrais que
  permitam alcançar destinos fora da raiz.
- Implementar a leitura recursiva em árvore com ordenação determinística:
  diretórios antes de arquivos e nomes em ordem alfabética dentro de cada
  grupo.
- Implementar as mutações com checagem prévia de existência e tipo, proteção
  da raiz e sem sobrescrita do destino:
  exclusão e renomeação de arquivo, movimentação de arquivo para diretório
  existente ou raiz, criação e renomeação de diretório e exclusão somente de
  diretório vazio.
- Traduzir falhas de I/O e violações das regras para
  `FtpFileStorageException`, sem deixar alterações parciais ou sobrescritas.

### 4. Criar o service de casos de uso

- Criar `FtpFileService` em `service`, injetando somente `FtpFileStorage`.
- Expor no service as mesmas operações de gerenciamento e delegá-las ao
  contrato, sem acessar `Path`, `Files` ou outros detalhes físicos.

### 5. Implementar os testes

- Criar testes unitários de `FtpFileService` com mock de `FtpFileStorage`,
  cobrindo a delegação e os argumentos de cada caso de uso.
- Criar testes de `LocalFtpFileStorage` com `@TempDir`, cobrindo:
  árvore recursiva e ordenação; operações válidas de arquivos e diretórios;
  preservação de conteúdo; movimentação para subdiretório e raiz;
  ausências, tipos incompatíveis, nomes vazios, colisões e diretório não
  vazio; proteção da raiz, caminhos absolutos, `..` e escapes por links
  simbólicos.
- Reexecutar os testes existentes de usuários após sua mudança de pacote para
  comprovar que a reorganização não alterou o comportamento.

### 6. Atualizar documentação e validar

- Atualizar `docs/project-structure.md` com os subpacotes `user` e `storage`, o
  novo service, seus componentes e a organização espelhada dos testes.
- Criar `tasks.md` com o acompanhamento da execução após a aprovação deste
  plano.
- Executar a suíte completa de testes e a compilação Maven.
- Conferir cada critério de aceite e registrar limitações ou pendências antes
  de solicitar revisão da implementação.

## Fora do escopo

- Controllers ou endpoints HTTP.
- Conexão pelo protocolo FTP.
- Movimentação de diretórios.
- Exclusão recursiva.
- Migrations ou alterações no banco de dados.
- Novas dependências ou alterações de versão.

## Validação prevista

- `./mvnw test` (ou `mvn test`, conforme os executáveis disponíveis no
  repositório).
- `./mvnw verify -DskipTests` (ou equivalente com `mvn`) para confirmar a
  compilação e o empacotamento.
- Revisão manual da estrutura de pacotes, da documentação e dos critérios de
  aceite da spec.
