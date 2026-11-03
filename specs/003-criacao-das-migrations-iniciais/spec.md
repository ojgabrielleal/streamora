# Spec 003 - Criação das migrations iniciais

**Status:** concluida
**Branch:** feature/003-criacao-das-migrations-iniciais

## Objetivo
Criar as primeiras migrations no projeto

## Requisitos
- Criar migrations na pasta resources/db/migrations

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

## Estrutura de migrations
- Tabela: users
    - id
    - uuid
    - fullname
        - Type: String
    - nickname
        - Type: String
    - username
        - Type: String
    - password | Observações: Esse campo irá armazenar uma senha em hash

- Tabela: playlists
    - id
    - uuid
    - name
        - Type: String

- Tabela: files
    - id
    - uuid
    - type
        - Type: ENUM
        - Values:
            - music
            - station_id
            - sweeper
            - jingle
            - commercial
            - bumper
    - name
        - Type: String
    - path
        - Type: String

- Tabela: playlist_files
    - playlist_id | FK -> playlists.id
    - file_id | FK -> files.id
