# Spec 010 - Criando controllers

**Status:** Finalizado

**Branch:** main

## Objetivo
Criar os controllers baseado nos services já existentes.

## Requisitos
- Cada controller vai atuar sobre um prefixo de url, por exemplo: `userController` vai ficar no `/user`
- Cada arquivo de controller deve ter o sufixo `Controller` no nome do arquivo
- Os controllers não deve fazer tratamento e manipulação de dados em qualquer aspecto
- Cada método do controller deve apenas chamar seu método correspondente na camada service

## Critérios de aceite
- [x] Os controllers não deve fazer tratamento e manipulação de dados em qualquer aspecto
- [x] Cada método do controller deve apenas chamar seu método correspondente na camada services
