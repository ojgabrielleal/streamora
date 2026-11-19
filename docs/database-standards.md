# Padrões de banco de dados

## Migrations
- Crie arquivos `src/main/resources/db/migration/V{versão}__{descrição}.sql`.
- Cada migration deve alterar somente uma tabela; use arquivos separados para
  tabelas diferentes.
- Toda coluna `uuid` deve ter restrição de unicidade.
- Ao alterar a estrutura do banco, atualize `database-schema.md`.

## Testes de migrations
- Mantenha testes globais apenas para convenções comuns das tabelas principais.
- Verifique somente `id` e `uuid` nas migrations de criação.
- Exclua tabelas associativas dessa verificação.
- Não teste detalhes específicos de migrations.
