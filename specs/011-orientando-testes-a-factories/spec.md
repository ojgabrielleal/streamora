# Spec 011 - Orientando testes a factories

**Status:** Concluída

## Objetivo
Criar factories para centralizar a geração de dados ficticios nos testes

## Requisitos
- Cada arquivo de factory vai estar localizado na pasta `src/test/java/com/streamora/streamora/factory/`
- Cada arquivo de factory deve ter o sufixo `Factory` no nome do arquivo
- Cada factory devem ter uma método que vai gerar apenas um dado, e outro que vai permitir informar uma quantidade de dados a ser criado. Isso deve ser feito atráves de sobrecarga. Esse método se chamar `toCreate`
- Cada factory deve ter dois métodos que vai gerar dados para `toEntity` e `toResponse`
- Cada método `toEntity` e `toResponse` também devem ter métodos de sobrecarga para dar a possibilidade de criar apenas um item, ou vários itens.
- Todos os testes em `src/test/java/com/streamora/streamora/` de `mapper` e `services` devem ser revistos, devem ser removidos todas as criações de dados presentes nesses testes para centralizar tudo nas `factories`

## Critérios de aceite
- [x] Todos os testes em `src/test/java/com/streamora/streamora/` de `mapper` e `services` devem ser revistos, devem ser removidos todas as criações de dados presentes nesses testes para centralizar tudo nas `factories`
- [x] Cada factory deve ter dois métodos que vai gerar dados para `toEntity` e `toResponse`
- [x] Cada método `toEntity` e `toResponse` também devem ter métodos de sobrecarga para dar a possibilidade de criar apenas um item, ou vários itens.
- [x] Cada factory devem ter uma método que vai gerar apenas um dado, e outro que vai permitir informar uma quantidade de dados a ser criado. Isso deve ser feito atráves de sobrecarga. Esse método se chamar `toCreate`
- [x] Cada arquivo de factory deve ter o sufixo `Factory` no nome do arquivo
