# Spec 009 - Mappers aceitando entidades existentes

**Status:** Finalizado
**Branch:** feature/009-corrigindo-mappers-para-aceitar-entidades-existentes

## Objetivo
Corrigir os mappers na propriedade `toEntity` para aceitar uma instancia existente ou criar uma nova atráves da lógica de sobrecarga 

## Requisitos
- Percorrer todas os mappers em `src/main/java/com/streamora/streamora/mapper` e adicionar uma sobrecarga pro método `toEntity`. Um método deverá aceitar uma instancia já existente e outro criar uma nova instancia, essa sobrecarga será diferenciada atráves de parâmetros dos métodos, um método terá um parâmetro aceitando uma instancia e request e outro somente o request. 
- Pecorrer todos os services em `src/main/java/com/streamora/streamora/service` e remover tudo que seja função dos mappers, como `setName`, `setUsername` e etc.

## Critérios de aceite
- [x] As funcionalidades de converter de `request` pra `entity` e vice e versa deverá ser somente do `mapper`
