# Spec 008 - Geração de UUID automaticamente nas entities

**Status:** Concluída

## Objetivo
Criar métodos privados nas entities para que a cada registro adicionado um UUID seja gerado automáticamente para aquele registro.

## Requisitos
- Percorrer todas as entities em `src/main/java/com/streamora/streamora/entity` e adicionar um método que gere UUID automáticamente a cada adição no banco de dados.
- Percorrer todos mappers em `src/main/java/com/streamora/streamora/mapper` e remover nos métodos `toEntity` a inserção de uuid pela propriedade `setUuid`.
- Percorrer todos os services em `src/main/java/com/streamora/streamora/service` e remover nos métodos caso exista a inserção de uuid pela propriedade `setUuid`

## Critérios de aceite
- [x] Os `mappers` e `services` não devem setar um uuid, essa função deve ser centralizada apenas nas `entities`
