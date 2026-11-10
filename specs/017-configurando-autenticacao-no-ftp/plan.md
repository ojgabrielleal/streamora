# Plano de implementação — Spec 017

## Objetivo

Sincronizar o cadastro, a atualização de senha e a exclusão de usuários da
aplicação com a base virtual do Pure-FTPd, criando o acesso FTP com as
credenciais informadas, mantendo sua senha atualizada e removendo-o quando o
usuário for excluído.

## Decisões de implementação

- Criar `PureFtpd` no pacote `infrastructure`, seguindo a convenção de nomes de
  classes Java, com operações públicas para criar, atualizar credenciais e
  remover usuários FTP.
- Encapsular em `PureFtpd` a execução do utilitário oficial `pure-pw`, sem
  espalhar comandos de infraestrutura pelo `UserService`.
- Fornecer a senha ao processo pela entrada padrão, nunca como argumento de
  linha de comando, log, propriedade versionada ou variável de ambiente.
- Após cada inclusão ou exclusão, reconstruir a base binária com `pure-pw mkdb`
  para que a alteração seja reconhecida pelo servidor.
- Disponibilizar o utilitário `pure-pw` na imagem da aplicação e montar nela o
  volume `pureftpd_passwords`, mantendo o mesmo arquivo de usuários utilizado
  pelo contêiner Pure-FTPd.
- Usar o username e a senha em texto recebidos no cadastro para criar o usuário
  FTP antes que a senha seja descartada; a senha persistida no MySQL continuará
  sendo somente o hash gerado pelo `PasswordEncoder`.
- Na exclusão, localizar o usuário uma única vez e usar seu username tanto para
  excluir o acesso FTP quanto para remover o registro da aplicação.
- Ao alterar somente a senha, atualizar a senha do mesmo usuário no Pure-FTPd.
- Manter o username imutável após o cadastro e rejeitar tentativas de
  alteração, evitando divergência entre a aplicação e o Pure-FTPd.
- Não acionar o Pure-FTPd quando somente `fullname` ou `nickname` forem
  alterados.
- Tratar falhas do comando externo como erro da operação, impedindo que uma
  criação, atualização ou exclusão seja reportada como concluída quando a
  sincronização FTP falhar.

## Etapas

1. Criar `PureFtpd` em `src/main/java/com/streamora/streamora/infrastructure`,
   responsável por montar e executar de forma segura os comandos de criação,
   exclusão e atualização da base do Pure-FTPd.
2. Injetar `PureFtpd` no `UserService` e integrar as operações FTP ao cadastro,
   à atualização de credenciais e à exclusão, preservando no banco somente o
   hash da senha.
3. Ajustar o `Dockerfile` e o `docker-compose.yml` para disponibilizar o
   `pure-pw` e compartilhar a base persistente de credenciais com a aplicação.
4. Atualizar o README para substituir o procedimento exclusivamente manual pela
   sincronização automática e documentar a configuração operacional.
5. Criar testes unitários de `PureFtpd` que validem comandos, entrada da senha,
   reconstrução da base e propagação de falhas sem depender de um servidor FTP
   real.
6. Atualizar os testes de `UserService` para verificar a criação, a atualização
   de senha, a rejeição da troca de username, a exclusão do acesso FTP e a
   ausência de chamadas à infraestrutura quando apenas dados pessoais forem
   alterados ou o usuário não existir.
7. Criar `tasks.md` e atualizar `docs/project-structure.md` com o novo pacote e
   seus testes.
8. Executar a suíte completa de testes, a compilação Maven e as validações do
   Docker Compose.

## Validação esperada

- Ao cadastrar um usuário, a senha em texto é entregue somente ao processo
  `pure-pw`, enquanto o MySQL recebe apenas seu hash.
- O usuário virtual é criado com diretório próprio sob `/home/ftpusers` e a base
  binária do Pure-FTPd é atualizada.
- Ao excluir um usuário, seu username é removido da base do Pure-FTPd e a base
  binária é atualizada.
- Ao alterar a senha, a credencial correspondente no Pure-FTPd é atualizada.
- Ao tentar alterar o username, a operação é rejeitada sem modificar o banco ou
  o Pure-FTPd.
- Atualizações de `fullname` e `nickname` não executam comandos do Pure-FTPd.
- Falhas na execução do utilitário externo são propagadas e não são ignoradas.
- Os testes unitários não executam comandos reais nem exigem Docker ou
  Pure-FTPd em execução.
- Todos os testes existentes continuam passando, o projeto compila e a
  configuração do Compose permanece válida.

## Limitação conhecida

O banco MySQL e a base do Pure-FTPd não participam da mesma transação. A ordem
das operações e a propagação de falhas reduzirão estados divergentes, mas não
podem oferecer atomicidade distribuída em caso de falha entre a confirmação de
um sistema e a do outro.
