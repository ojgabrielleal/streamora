# Spec 020 - Gerenciamento de arquivos do FTP

**Status:** Planejada

## Objetivo

Disponibilizar operações seguras para consultar e manipular os arquivos e
diretórios armazenados no volume compartilhado com o Pure-FTPd, separando a
coordenação dos casos de uso das operações físicas no sistema de arquivos e
organizando as responsabilidades de usuários e armazenamento em subpacotes
próprios da integração com o Pure-FTPd.

## Requisitos

- Criar `FtpFileService` no pacote `service` para coordenar os casos de uso de
  gerenciamento dos arquivos do FTP.
- Criar o subpacote `infrastructure/pureFtpd/user` e mover para ele os
  componentes existentes de gerenciamento de usuários e credenciais do
  Pure-FTPd.
- Mover também os subpacotes `interfaces`, `records` e `exception` atualmente
  relacionados ao gerenciamento de usuários para dentro de
  `infrastructure/pureFtpd/user`.
- Atualizar os packages, imports e testes afetados pela reorganização do
  gerenciamento de usuários, sem alterar seu comportamento.
- Criar o subpacote `infrastructure/pureFtpd/storage` para concentrar a nova
  implementação de gerenciamento de arquivos e diretórios.
- Criar a interface `FtpFileStorage` em
  `infrastructure/pureFtpd/storage/interfaces` para abstrair as operações no
  sistema de arquivos.
- Criar `LocalFtpFileStorage` em `infrastructure/pureFtpd/storage` como
  implementação local de `FtpFileStorage` baseada em `java.nio.file`.
- Reutilizar `PureFtpdProperties.homeDirectory` como diretório raiz permitido
  para todas as operações.
- Criar uma representação imutável `FtpFileEntry` em
  `infrastructure/pureFtpd/storage/records` para os itens encontrados.
- Cada `FtpFileEntry` deve informar o nome, o tipo do item e os filhos quando o
  item for um diretório.
- A listagem deve percorrer recursivamente a raiz configurada e devolver a
  estrutura de arquivos e diretórios em formato de árvore.
- Disponibilizar uma operação para excluir fisicamente um arquivo.
- Disponibilizar uma operação para renomear fisicamente um arquivo, preservando
  seu conteúdo e diretório atual.
- Disponibilizar uma operação para mover um arquivo para um diretório existente,
  incluindo a possibilidade de movê-lo para a raiz permitida.
- Disponibilizar uma operação para criar um diretório.
- Disponibilizar uma operação para renomear fisicamente um diretório.
- Disponibilizar uma operação para excluir somente diretórios vazios.
- Criar `FtpFileStorageException` em
  `infrastructure/pureFtpd/storage/exception` para representar falhas de acesso
  e manipulação do armazenamento.
- Criar testes unitários do `FtpFileService` com `FtpFileStorage` substituído por
  mock.
- Criar testes de `LocalFtpFileStorage` usando diretórios temporários, sem
  manipular o volume real do ambiente.
- Reorganizar os testes existentes do gerenciamento de usuários em
  `infrastructure/pureFtpd/user` e criar os testes de armazenamento em
  `infrastructure/pureFtpd/storage`.
- Atualizar `docs/project-structure.md` para documentar os novos componentes e
  suas responsabilidades.

## Regras

- As operações devem manipular diretamente o volume local compartilhado com o
  Pure-FTPd; não deve ser criada uma conexão pelo protocolo FTP.
- O pacote `infrastructure/pureFtpd/user` deve conter somente responsabilidades
  relacionadas a usuários, credenciais e execução dos comandos do Pure-FTPd.
- O pacote `infrastructure/pureFtpd/storage` deve conter somente
  responsabilidades relacionadas a arquivos e diretórios do volume
  compartilhado.
- A reorganização dos componentes existentes não pode alterar o comportamento
  do gerenciamento de usuários do Pure-FTPd.
- Todos os caminhos recebidos devem ser relativos à raiz configurada.
- Todo caminho deve ser resolvido, normalizado e validado antes de qualquer
  leitura ou alteração.
- Nenhuma operação pode acessar ou modificar itens fora da raiz configurada.
- Caminhos com tentativa de escape, incluindo segmentos `..`, devem ser
  rejeitados.
- Links simbólicos não podem ser usados para acessar destinos fora da raiz nem
  para contornar as validações de caminho.
- A raiz configurada não pode ser excluída, renomeada ou movida.
- Exclusões de diretório não devem ser recursivas; um diretório com qualquer
  conteúdo deve ser preservado e a operação deve falhar.
- Renomeações e movimentações não podem sobrescrever um item existente no
  destino.
- Um arquivo só pode ser movido para um diretório que já exista dentro da raiz
  permitida.
- A movimentação de diretórios não faz parte desta spec.
- Nomes vazios, caminhos inexistentes e tipos incompatíveis com a operação
  solicitada devem produzir erros explícitos.
- A ordenação dos filhos da árvore deve ser determinística, mantendo diretórios
  antes de arquivos e usando o nome em ordem alfabética dentro de cada grupo.
- Não devem ser adicionadas dependências para as operações de sistema de
  arquivos.
- Controllers e endpoints HTTP para essas operações não fazem parte desta spec.

## Critérios de aceite

- [ ] A aplicação lista recursivamente os arquivos e diretórios da raiz
  configurada em formato de árvore.
- [ ] Os componentes existentes de gerenciamento de usuários estão organizados
  em `infrastructure/pureFtpd/user`, com seus testes no pacote correspondente.
- [ ] Os componentes novos de gerenciamento de arquivos estão organizados em
  `infrastructure/pureFtpd/storage`, com seus testes no pacote correspondente.
- [ ] O gerenciamento existente de usuários e credenciais continua funcionando
  após a reorganização dos packages.
- [ ] Cada item da árvore informa nome e tipo, e cada diretório contém sua lista
  ordenada de filhos.
- [ ] Um arquivo existente pode ser excluído fisicamente.
- [ ] Um arquivo pode ser renomeado sem alteração de seu conteúdo.
- [ ] Um arquivo pode ser movido para um diretório existente ou para a raiz.
- [ ] Um diretório pode ser criado e renomeado fisicamente.
- [ ] Um diretório vazio pode ser excluído.
- [ ] Um diretório com conteúdo não pode ser excluído.
- [ ] Operações com origem inexistente, destino inválido ou colisão de nomes
  falham sem sobrescrever ou remover outros itens.
- [ ] Tentativas de acessar caminhos fora da raiz são rejeitadas.
- [ ] Links simbólicos não permitem escapar da raiz configurada.
- [ ] O service depende da abstração `FtpFileStorage`, sem executar operações
  físicas diretamente.
- [ ] A implementação física utiliza somente APIs nativas de `java.nio.file`.
- [ ] Os testes da implementação física usam diretórios temporários.
- [ ] `docs/project-structure.md` descreve os novos componentes.
- [ ] O projeto compila e todos os testes passam.
