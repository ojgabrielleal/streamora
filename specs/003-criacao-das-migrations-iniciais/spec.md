# Spec 003 - Criação das migrations iniciais

**Status:** concluida
**Branch:** feature/003-criacao-das-migrations-iniciais

## Objetivo
Criar as primeiras migrations no projeto

## Requisitos
- Criar migrations na pasta `src/main/resources/db/migration`

## Regras
- As migrations devem ser feitas em arquivos `.sql`
- Cada migration deve ser responsável por alterações em apenas uma tabela.
- Alterações em tabelas diferentes devem obrigatoriamente ser feitas em migrations separadas.
- Todas as colunas `uuid` devem possuir restrição de unicidade.

## Critérios de aceite
- [x] Os arquivos devem estar no formato `.sql`
- [x] Cada migration deve alterar apenas uma tabela
- [x] Todas as tabelas descritas nesta spec devem possuir sua respectiva migration
- [x] Todas as colunas `uuid` devem ser únicas

## Estrutura do banco de dados

A estrutura consolidada das tabelas, colunas e relacionamentos está documentada
no [diagrama do banco de dados](../../docs/database-schema.md). O diagrama deve
ser atualizado sempre que uma migration alterar a estrutura do banco.
