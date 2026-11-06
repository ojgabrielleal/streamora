# Plano de implementação - Spec 011

## Objetivo

Centralizar a criação dos dados usados pelos testes de mappers e services em
factories reutilizáveis, mantendo os cenários determinísticos e sem alterar o
código de produção.

## Etapas

1. Criar o pacote de testes `factory` e as factories
   `UserFactory`, `PlaylistFactory` e `PlaylistFileFactory`.
2. Em cada factory, implementar os métodos sobrecarregados:
   - `toCreate()` e `toCreate(int quantity)` para DTOs de requisição;
   - `toEntity()` e `toEntity(int quantity)` para entidades;
   - `toResponse()` e `toResponse(int quantity)` para DTOs de resposta.
3. Gerar dados determinísticos e distintos por índice nas sobrecargas de
   coleção, permitindo que os testes comparem valores sem duplicar a montagem
   dos objetos.
4. Revisar os testes de `mapper` para obter requests, entities, responses e
   relacionamentos exclusivamente pelas factories, preservando as asserções
   atuais sobre conversão, identidade e associações.
5. Revisar os testes de `service` para remover helpers locais e instanciações
   de dados de domínio, usando as factories nos cenários de listagem, consulta,
   criação, atualização, remoção e associações.
6. Criar testes unitários das factories para validar as variantes de item único
   e quantidade, incluindo tipo retornado, quantidade solicitada e distinção
   dos dados gerados.
7. Atualizar `docs/project-structure.md` para registrar o novo pacote
   `src/test/java/com/streamora/streamora/factory` e sua responsabilidade.
8. Executar a suíte completa de testes e a compilação do projeto, conferindo
   todos os critérios de aceite da spec.

## Arquivos previstos

- `src/test/java/com/streamora/streamora/factory/UserFactory.java`
- `src/test/java/com/streamora/streamora/factory/PlaylistFactory.java`
- `src/test/java/com/streamora/streamora/factory/PlaylistFileFactory.java`
- testes das factories no mesmo pacote de testes
- testes existentes em `mapper` e `service`
- `docs/project-structure.md`
- `specs/011-orientando-testes-a-factories/tasks.md`

## Restrições

- Não alterar classes de produção nem adicionar dependências.
- Não modificar testes fora de `mapper`, `service` e dos novos testes de
  factories, exceto pela documentação estrutural exigida.
- Preservar o comportamento e a cobertura dos cenários existentes.
