# Spec 017 - Configurando autenticação no FTP

**Status:** Concluída

## Objetivo

Realizar a configuração para que os acessos no FTP sejam sincronizados sempre
que um usuário for criado, tiver sua senha alterada ou for removido.

## Requisitos
- Criar uma nova classe de infraestrutura chamada `PureFtpd` na pasta
  `src/main/java/com/streamora/streamora/infrastructure`. Essa classe deve
  disponibilizar operações para criar, atualizar e remover usuários do FTP.
- O método `createFtpdUser` deve usar o `username` e `password` do usuário criado.
- Importar o método `createFtpdUser` e usar no método `store` dentro do service `UserService`.
- Sincronizar no FTP as alterações de `password` realizadas pelo método
  `update` do service `UserService`.
- O `username` deve ser definido no cadastro e permanecer imutável. Uma
  tentativa de alterá-lo deve ser rejeitada.
- Alterações somente em `fullname` ou `nickname` não devem modificar o acesso
  FTP.
- Importar o método `deleteFtpdUser` e usar no método `delete` dentro do service
  `UserService`.
- Uma falha na sincronização com o FTP deve fazer a operação correspondente
  falhar, sem ser ignorada silenciosamente.
