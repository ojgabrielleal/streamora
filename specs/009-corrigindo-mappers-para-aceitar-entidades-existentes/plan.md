# Plano de implementação — Spec 009

## Objetivo

Centralizar nos mappers a conversão de requests tanto para novas entities quanto
para entities existentes, mantendo os services responsáveis apenas pela
orquestração dos casos de uso.

## Etapas

1. Ampliar os testes de `PlaylistFileMapper`, `PlaylistMapper` e `UserMapper`
   para validar a atualização de uma instância existente, incluindo a preservação
   de identidade e relacionamentos que não fazem parte do request.
2. Adicionar em cada mapper a sobrecarga `toEntity(request, entity)`, que aplica
   os campos editáveis do request na instância recebida e retorna essa mesma
   instância.
3. Fazer a sobrecarga `toEntity(request)` criar uma nova entity e reutilizar a
   sobrecarga de atualização, evitando duplicação da lógica de mapeamento.
4. Alterar os métodos `update` dos três services para delegar aos mappers a
   aplicação dos campos, removendo os setters que representam conversão.
5. Atualizar os testes dos services, quando necessário, para preservar a
   cobertura dos fluxos de criação e atualização.
6. Atualizar `docs/project-structure.md` para registrar que os mappers também
   atualizam instâncias existentes.
7. Criar `tasks.md`, executar a suíte de testes e verificar a compilação e os
   critérios de aceite.

## Validação

- Executar `./mvnw test` (ou `mvn test`, conforme o wrapper disponível).
- Confirmar que criações continuam produzindo entities sem identidade definida.
- Confirmar que atualizações reutilizam a mesma instância e preservam `id`,
  `uuid` e relacionamentos.
- Confirmar que os services não aplicam diretamente campos de request nas
  entities.

## Fora de escopo

- Alterações em DTOs, entities, repositories ou migrations.
- Novas dependências ou mudanças de versão.
