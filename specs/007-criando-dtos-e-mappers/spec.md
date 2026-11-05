# Spec 007 - Criação DTOs e Mappers

**Status:** Concluída
**Branch:** feature/007-criando-dtos-e-mappers

## Objetivo
Criar DTOs e mappers e aplicá-los aos services já existentes.

## Requisitos
- Criar os dtos devem estar na pasta `src/main/java/com/streamora/streamora/dto`
- Toda classe e todo arquivo de DTO devem possuir o sufixo `DTO`, assim como
  os mappers possuem o sufixo `Mapper`.
- Criar os mappers devem estar na pasta `src/main/java/com/streamora/streamora/mapper`
- Todos os dtos devem se basear nas entities já criadas em `src/main/java/com/streamora/streamora/entity`
- Os mappers devem ter dois métodos, passando da entity para response, e passando da request pra entity
- Os métodos públicos dos services devem receber DTOs de request nas operações
  de criação e atualização.
- Os métodos públicos dos services devem retornar DTOs de response, exceto
  as operações sem retorno.
- Os services devem usar os mappers para converter DTOs e entities.
- As entities novas devem receber um UUID antes de serem persistidas, pois os
  DTOs de request não expõem esse campo.
- As atualizações devem preservar o ID, o UUID e os relacionamentos da entity
  persistida.

## Critérios de aceite
- [x] Os mappers devem ter dois métodos, passando da entity para response, e passando da request pra entity
- [x] Todas as classes e arquivos de DTO devem possuir o sufixo `DTO`.
- [x] As respostas devem expor somente o UUID, sem expor o ID interno.
- [x] A resposta de usuário não deve expor a senha.
- [x] Os services devem usar DTOs em seus contratos públicos e mappers nas
  conversões.
- [x] O cadastro deve gerar o UUID das novas entities.
- [x] A atualização deve preservar a identidade e os relacionamentos existentes.
