# Spec 003 - Criação das migrations iniciais

**Status:** concluida
**Branch:** main

## Objetivo
Criar as primeiras migrations no projeto

## Requisitos
- Criar migrations na pasta `src/main/resources/db/migration`

## Critérios de aceite
- [x] Os arquivos devem estar no formato `.sql`
- [x] Cada migration deve alterar apenas uma tabela
- [x] Todas as tabelas descritas nesta spec devem possuir sua respectiva migration
- [x] Todas as colunas `uuid` devem ser únicas
- [x] As migrations das tabelas principais devem possuir os campos `id` e `uuid`

## Estrutura do banco de dados

A estrutura consolidada das tabelas, colunas e relacionamentos está documentada
no [diagrama do banco de dados](../../docs/database-schema.md). O diagrama deve
ser atualizado sempre que uma migration alterar a estrutura do banco.
